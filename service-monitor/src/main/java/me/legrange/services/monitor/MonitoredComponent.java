package me.legrange.services.monitor;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.WithJetty;

import java.util.function.Supplier;

/**
 * @param <C>
 * @param <S>
 * @author gideon
 */
public abstract class MonitoredComponent<S extends Service, C> extends Component<S, C>  implements WithMonitor, WithJetty {

    public MonitoredComponent(S service) {
        super(service);
    }

    public final void monitor(String name, Supplier<State> function) throws ComponentException {
        if (service() instanceof WithMonitor) {
            ((WithMonitor) service()).monitor(name, function);
        }
    }
}
