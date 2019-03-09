package me.legrange.service.monitor;

import java.util.function.Supplier;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

/**
 *
 * @author gideon
 * @param <C>
 * @param <S>
 */
public abstract class MonitoredComponent<S extends Service, C> extends Component<S, C> {

    protected MonitoredComponent(S service) {
        super(service);
    }
    
    protected final void monitor(String name, Supplier<State> function) throws ComponentException {
        if (service() instanceof WithMonitor) {
            ((WithMonitor)service()).monitor(name,function);
        }

    }
}
