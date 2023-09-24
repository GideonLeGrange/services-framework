package me.legrange.service;

import me.legrange.config.ConfigurationException;
import me.legrange.config.YamlLoader;
import me.legrange.service.RuntimeEnvironment.Type;
import sun.misc.Signal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static me.legrange.log.Log.critical;
import static me.legrange.log.Log.error;
import static me.legrange.log.Log.info;
import static me.legrange.log.Log.warning;

/**
 * @param <Conf> The type of the configuration class for this service.
 * @author gideon
 */
public abstract class Service<Conf> {

    private Conf conf;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final ExecutorService threadPool = new ForkJoinPool(32);
    private boolean running;

    public static void main(String... args) {
        try {
            InputStream config = null;
            switch (args.length) {
                case 1:
                    config = new FileInputStream(args[0]);
                    break;
                case 2:
                    switch (args[0]) {
                        case "-file":
                            config = new FileInputStream(args[1]);
                            break;
                        case "-resource":
                            config = Service.class.getClassLoader().getResourceAsStream(args[1]);
                            break;
                        default:
                    }
                    break;
            }
            if (config == null) {
                failedStartup("Usage: Server [-file|-resource] <config file> ");
            }
            Class<? extends Service> serviceClass = determineServiceClass();
            Service service = serviceClass.getDeclaredConstructor().newInstance();
            try {
                service.configure(config);
            } catch (ServiceException ex) {
                ex.printStackTrace();
                failedStartup(String.format("Error configuring server: %s", ex.getMessage()));
            }
            service.startComponents();
            service.setupShutdownSignals();
            info("Platform is %s on %s %s", RuntimeEnvironment.getOsType(),
                    RuntimeEnvironment.getArch(),
                    RuntimeEnvironment.isInContainer() ? "running in a container" : "");
            service.start();
            service.running = true;
            while (service.isRunning()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
            service.stop();
            service.stopComponents();
            System.exit(0);
        } catch (ServiceException | InstantiationException | NoSuchMethodException | SecurityException |
                 IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            error(ex, "Fatal error: %s", ex.getMessage());
            System.exit(1);
        } catch (FileNotFoundException ex) {
            error(ex, "Fatal error: %s", ex.getMessage());
        }
    }

    /**
     * Get the running component for the given component class.
     *
     * @param <C>   The type of the component
     * @param clazz The class representing the component
     * @return The component
     * @throws me.legrange.service.ComponentNotFoundException Thrown if the component requested cannot be found
     */
    public final <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException {
        if (components.containsKey(clazz)) {
            return clazz.cast(components.get(clazz));
        }
        throw new ComponentNotFoundException(format("No component registered of type '%s'. BUG!", clazz.getSimpleName()));
    }

    /**
     * Start the service components
     */
    private void startComponents() throws ServiceException {
        for (Class<? extends Component> clazz : getRequiredComponents()) {
            if (!components.containsKey(clazz)) {
                startComponent(clazz);
            }
        }
    }

    /**
     * Stop the service components
     *
     */
    private void stopComponents() throws ServiceException {
        for (Class<? extends Component> clazz : getRequiredComponents()) {
            if (components.containsKey(clazz)) {
                try {
                    stopComponent(clazz);
                } catch (ComponentException ex) {
                    error(ex, "Error stopping component %s (%s)", clazz.getSimpleName(), ex.getMessage());
                }
            }
        }
    }

    /**
     * Start a specific component
     *
     * @param clazz The class of the component to start.
     */
    private <C extends Component> C startComponent(Class<C> clazz) throws ServiceException {
        try {
            Constructor<? extends Component> cons = clazz.getConstructor(Service.class);
            C comp = (C) cons.newInstance(this);
            if (comp.requiresConfig()) {
                Object compConf = getConfigFor(comp);
                comp.start(compConf);
            } else {
                comp.start(null);
            }
            components.put(clazz, comp);
            return comp;
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException |
                 InvocationTargetException ex) {
            throw new ServiceException(format("Error creating new component of type '%s': %s", clazz.getSimpleName(), ex.getMessage()), ex);
        } catch (NoSuchMethodException ex) {
            throw new ServiceException(format("Could not find constructor with Service as parameter on component of type '%s': %s", clazz.getSimpleName(), ex.getMessage()), ex);
        }
    }

    /**
     * Stop a specific component
     *
     * @param clazz The class of the component to stop.
     */
    private <C extends Component> void stopComponent(Class<C> clazz) throws ServiceException {
        C comp = (C) components.get(clazz);
        comp.stop();
    }

    /**
     * Get the class for the configuration of this service.
     *
     * @return The configuration class
     */
    private Class<Conf> getConfigClass() throws ServiceException {
        String name = getClass().getName();
        int idx = name.lastIndexOf("Service");
        if (idx > 0) {
            name = name.substring(0, idx);
            name = name + "Config";
        } else {
            if (!name.endsWith("Config")) {
                name = name + "Config";
            }
        }
        try {
            return (Class<Conf>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new ServiceException(format("Could not find config class '%s' for service class '%s'. BUG!", name, getClass().getName()), ex);
        }
    }

    /**
     * Find the components we need to activate for this service. The components
     * are supplied in the order in which they need to be activated.
     *
     * @return The set of components required.
     */
    private List<Class<? extends Component>> getRequiredComponents() throws ServiceException {
        return getRequiredComponents(getClass());
    }

    /**
     * Find the components we need to activate for this class (component or
     * service).
     *
     * @return The list of components required.
     */
    private List<Class<? extends Component>> getRequiredComponents(Class<?> clazz) throws ServiceException {
        List<Class<? extends Component>> res = new LinkedList<>();
        Set<Class<? extends WithComponent>> interfaces = getWithInterfaces(clazz);
        for (Class<? extends WithComponent> iface : interfaces) {
            String name = iface.getName().replace(".With", ".").concat("Component");
            try {
                Class<?> compClass = Class.forName(name);
                if (Component.class.isAssignableFrom(compClass)) {
                    res.addAll(0, getRequiredComponents(compClass));
                    if (!res.contains(compClass)) {
                        res.add((Class<? extends Component>) compClass);
                    }
                } else {
                    throw new ServiceException(format("Class '%s' associated with interface '%s' does not extend '%s'", name, iface.getName(), Component.class.getName()));
                }
            } catch (ClassNotFoundException ex) {
                throw new ServiceException(format("Cannot find component class '%s' associated with interface '%s'", name, iface.getName()), ex);
            }
        }
        return res;
    }

    public final <C extends Component> C requireComponent(Class<C> clazz) throws ServiceException {
        if (components.containsKey(clazz)) {
            return (C) components.get(clazz);
        }
        return startComponent(clazz);
    }

    /**
     * Find all WithComponent interfaces applied to a service or component class and it's
     * super classes.
     *
     * @param type The service or component class
     * @return The set of interface classes.
     */
    private Set<Class<? extends WithComponent>> getWithInterfaces(Class<?> type) {
        Set<Class<? extends WithComponent>> res = new HashSet<>();
        for (Class<?> iface : type.getInterfaces()) {
            if (WithComponent.class.isAssignableFrom(iface)) {
                res.add((Class<? extends WithComponent>) iface);
            }
        }
        if (Service.class.isAssignableFrom(type.getSuperclass())) {
            res.addAll(getWithInterfaces(type.getSuperclass()));
        } else if (Component.class.isAssignableFrom(type.getSuperclass())) {
            res.addAll(getWithInterfaces(type.getSuperclass()));
        }
        return res;
    }

    public final boolean isRunning() {
        return running;
    }

    /**
     * Start the service. This needs to be implemented by the implementation
     * subclass to get work done.
     *
     * @throws ServiceException Thrown if the service cannot be started
     */
    protected abstract void start() throws ServiceException;

    /**
     * Stop the service. This can be implemented by the implementation subclass to
     * release resources, do cleanup or accomplish other shut down tasks.
     *
     * @throws ServiceException Thrown if there is a problem stopping the service
     */
    protected void stop() throws ServiceException {

    }


    /**
     * Return the service configuration.
     *
     * @return The configuration object
     */
    protected Conf getConfig() {
        return conf;
    }

    /**
     * Submit a task for running to the thread pool.
     *
     * @param task The task to run.
     */
    public void submit(final Runnable task) {
        threadPool.submit(() -> {
            try {
                task.run();
            } catch (Throwable ex) {
                critical(ex, "Uncaught exception in task '%s': %s", task.getClass().getSimpleName(), ex.getMessage());
            }
        });
    }

    public final void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Configure the server.
     */
    private void configure(InputStream config) throws ServiceException {
        try {
            conf = YamlLoader.readConfiguration(config, getConfigClass());
        } catch (ConfigurationException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Output text to the console (used for startup errors
     *
     * @param fmt  format string
     * @param args arguments
     */
    private static void say(String fmt, Object... args) {
        System.out.printf(fmt, args);
        if (!fmt.endsWith("\n")) {
            System.out.println();
        }
    }


    private Object getConfigFor(Component com) throws ServiceException {
        String name = com.getName();
        String getName = "get" + name;
        for (Method meth : conf.getClass().getMethods()) {
            if (meth.getName().equalsIgnoreCase(getName)) {
                try {
                    return meth.invoke(conf);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new ServiceException(format("Cannot find configuration for component '%s' of type '%s' in config object of type '%s'. BUG!",
                            com.getName(), com.getClass().getSimpleName(), conf.getClass().getSimpleName()), ex);
                }
            }
        }
        throw new ServiceException(format("Cannot find configuration for component '%s' of type '%s' in config object of type '%s'",
                com.getName(), com.getClass().getSimpleName(), conf.getClass().getSimpleName()));
    }

    /**
     * Determine what sub-class of Service is the actual service we want to run.
     */
    private static Class<? extends Service> determineServiceClass() throws ServiceException {
            Class[] classContext = new SecurityManager() {
            @Override
            public Class[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        int idx = 0;
        Class clazz = null;
        while (idx < classContext.length) {
            Class next = classContext[idx];
            if (Service.class.isAssignableFrom(next)) {
                clazz = next;
            }
            idx++;
        }
        if (clazz == null) {
            throw new ServiceException("Could not find the service class to instantiate. Does your application extend Service?");
        }
        if (clazz == Service.class) {
            throw new ServiceException("Could not find the service class to instantiate. Did you implement the main method in your class?");
        }
        return clazz;
    }

    private void setupShutdownSignals() {
        String[] signals = RuntimeEnvironment.getOsType() == Type.WINDOWS ? new String[]{"TERM", "INT"} : new String[]{"TERM", "INT", "HUP"};
        for (String name : signals) {
            try {
                Signal.handle(new Signal(name), sig -> {
                    warning("%s signal (%d) received; shutting down", sig.getName(), sig.getNumber());
                    running = false;
                });
            }
            catch (IllegalArgumentException ex) {
                warning("Cannot setup signal (%s)", ex.getMessage());
            }
        }
    }

    private static void failedStartup(String reason) {
        say(reason);
        System.exit(1);
    }

}
