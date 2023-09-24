package me.legrange.services.monitor;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.GsonJerseyProvider;
import me.legrange.services.jetty.WithJetty;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static me.legrange.log.Log.error;
import static me.legrange.log.Log.info;
import static me.legrange.log.Log.warning;

/**
 * A service component that provides service monitoring.
 *
 * @author gideon
 */
public final class MonitorComponent extends Component<Service, MonitorConfig> implements WithJetty {

    private final Map<String, Supplier<State>> monitors = new HashMap();
    private static MonitorComponent instance;

    public MonitorComponent(Service service) {
        super(service);
    }

    @Override
    public void start(MonitorConfig config) throws ComponentException {
        instance = this;
        if (config.isEnabled()) {
            jetty().addProvider(GsonJerseyProvider.class);
            jetty().addEndpoint(config.getPath(), StateEndpoint.class);
            info("Monitoring available via HTTP on %s", config.getPath());
        }
        else {
            warning("Monitoring disabled");
        }
    }

    /**
     * Add a monitor input to the monitor.
     *
     * @param name     The name of the thing we are monitoring
     * @param function The function to call when we require monitoring state
     */
    void addMonitor(String name, Supplier<State> function) throws ComponentException {
        if (monitors.containsKey(name)) {
            throw new ComponentException(format("Duplicate monitor name '%s'", name));
        }
        monitors.put(name, function);
    }

    List<String> getMonitorNames() {
        return new LinkedList<>(monitors.keySet());
    }

    Object getMonitorState(String name, boolean flatten) throws ComponentException {
        Supplier<State> sup = monitors.get(name);
        if (sup == null) {
            throw new ComponentException(format("No monitor '%s' is defined", name));
        }
        try {
            if (flatten) {
                return flatten(sup.get());
            }
            return sup.get();
        } catch (Throwable ex) {
            error(ex);
            return new State(Status.ERROR, format("Monitoring failure: ", ex.getMessage()), Collections.EMPTY_LIST);
        }
    }

    Object getMonitorState(String name, String variable) throws ComponentException {
        Supplier<State> sup = monitors.get(name);
        if (sup == null) {
            throw new ComponentException(format("No monitor '%s' is defined", name));
        }
        State state = sup.get();
        Optional<Measurement> any = state.getData().stream()
                .filter(m -> m.getName().equals(variable))
                .findAny();
        try {
            if (any.isPresent()) {
                return sup.get();
            }
            throw new ComponentException(format("No variable '%s' under '%s' is defined", variable, name));
        } catch (Throwable ex) {
            error(ex);
            return new State(Status.ERROR, format("Monitoring failure: ", ex.getMessage()), Collections.EMPTY_LIST);
        }
    }

    @Override
    public String getName() {
        return "monitor";
    }

    private Map<String, Long> flatten(State state) {
        Map<String, Long> res = new HashMap();
        res.put("statusCode", Integer.valueOf(state.getStatus().getStatusCode()).longValue());
        res.put("errors", state.getErrors());
        res.put("warnings", state.getWarnings());
        for (Measurement m : state.getData()) {
            //prtg only supports numbers. i cri evrtim
            if (m.getValue() instanceof Integer) {
                res.put(m.getName(), ((Integer) m.getValue()).longValue());
            }
            if (m.getValue() instanceof Long) {
                res.put(m.getName(), (Long) m.getValue());
            }
        }
        return res;
    }

    static MonitorComponent getInstance() {
        return instance;
    }
}
