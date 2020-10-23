package me.legrange.services.jetty;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static me.legrange.log.Log.info;

/**
 * A component that adds Jetty HTTP functionality.
 *
 * @author gideon
 */
public class JettyComponent extends Component<Service, JettyConfig> {

    private Server server;
    private ServletContextHandler context;
    private boolean running = false;
    private final Set<Object> jerseyProviders = new HashSet();
    private final Map<String, Class> endpoints = new HashMap<>();

    public JettyComponent(Service service) {
        super(service);
    }


    @Override
    public String getName() {
        return "jetty";
    }

    @Override
    public void start(JettyConfig config) throws ComponentException {
        try {
            context = makeContext();
            server = new Server(config.getPort());
            server.setHandler(context);
            server.start();
            info("Started Jetty server on port %d", config.getPort());
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    /**
     * Add a new endpoint with the given path and endpoint class.
     *
     * @param path     The path
     * @param endpoint The endpoint class
     */
    public void addEndpoint(String path, Class endpoint) throws ComponentException {
        ResourceConfig rc = new ResourceConfig(endpoint);
        new ResourceConfig()
        checkForMessageBodyWriter();
        for (Object provider : jerseyProviders) {
            rc.register(provider);
        }
        ServletHolder holder = new ServletHolder(new ServletContainer(rc));
        context.addServlet(holder, path + "/*");
        try {
            context.start();
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
        if (endpoints.containsKey(path)) {
            info("Re-added endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
        }
        else {
            info("Added new endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
            endpoints.put(path, endpoint);
        }
        running = true;
    }

    public void addProvider(Class provider) throws ComponentException {
        try {
            addProvider(provider.getConstructor().newInstance());
            jerseyProviders.add(provider.getConstructor().newInstance());
        } catch (NoSuchMethodException e) {
            throw new ComponentException(format("No default constructor for provider class %s. Either pass an instance or a provider with a default constructor",provider.getSimpleName()),e);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ComponentException(format("Error creating provider from class %s (%s)",provider.getSimpleName(), e.getMessage()),e);
        }
    }

    public void addProvider(Object provider) throws ComponentException {
        jerseyProviders.add(provider);
        if (running) {
            info("Re-adding %d endpoint(s) because of provider change", endpoints.size());
            context = makeContext();
            try {
                server.stop();
                server.setHandler(context);
                server.start();
            }
            catch (Exception ex) {
                throw new ComponentException(ex.getMessage(), ex);
            }
            for (Map.Entry<String, Class> pair : endpoints.entrySet()) {
                addEndpoint(pair.getKey(), pair.getValue());
            }
        }
    }

    /**
     * Check for a MessageBodyWriter
     */
    private void checkForMessageBodyWriter() throws ComponentException {
        if (jerseyProviders.stream().noneMatch(p -> MessageBodyWriter.class.isInstance(p))) {
            throw new ComponentException("No MessageBodyWriters were registered for serialization. Remember to register them using the addProvider methods");
        }
    }

    private ServletContextHandler makeContext() {
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addFilter(ErrorFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        return context;
    }
}
