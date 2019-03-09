package me.legrange.service.monitor;

import static java.lang.String.format;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.ServiceException;
import me.legrange.service.jetty.JettyComponent;

/**
 * A service component that provides service monitoring.
 *
 * @author gideon
 * @param <S>
 */
public final class MonitorComponent<S extends Service> extends Component<S, MonitorConfig> {

    private final Map<String, Supplier<State>> monitors = new HashMap();
    private static MonitorComponent instance;

    public MonitorComponent(S service) {
        super(service);
    }

    @Override
    public void start(MonitorConfig config) throws ComponentException {
        instance = this;
        JettyComponent jetty;
        try {          
            jetty = getComponent(JettyComponent.class);
        } catch (ServiceException ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
        jetty.addRestEndpoint(StateEndpoint.class);
    }

    /**
     * Add a monitor input to the monitor.
     *
     * @param name The name of the thing we are monitoring
     * @param function The function to call when we require monitoring state
     * @throws za.co.adept.services.ComponentException
     */
    void addMonitor(String name, Supplier<State> function) throws ComponentException {
        if (monitors.containsKey(name)) {
            throw new ComponentException(format("Duplicate monitor name '%s'", name));
        }
        monitors.put(name, function);
    }

    List<String> getMonitorNames() {
        return new LinkedList(monitors.keySet());
    }

    State getMonitorState(String name) throws ComponentException {
        Supplier<State> sup = monitors.get(name);
        if (sup == null) {
            throw new ComponentException(format("No monitor '%s' is defined", name));
        }
        try {
            return sup.get();
        }
        catch (Throwable ex) {
            error(ex);
            return new State(Status.ERROR, format("Monitoring failure: ", ex.getMessage()), Collections.EMPTY_LIST  );
        }
    }

    @Override
    public String getName() {
        return "monitor";
    }

    static MonitorComponent getInstance() {
        return instance;
    }
}
