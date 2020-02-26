package me.legrange.services.monitor;

import me.legrange.service.ComponentException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

import java.util.function.Supplier;

/**
 *
 * @author gideon
 */
public interface WithMonitor extends WithComponent {
   
   default void monitor(String name, Supplier<State> function) throws ComponentException {
       try {
         getComponent(MonitorComponent.class).addMonitor(name, function);
       }
       catch (ServiceException ex) {
           throw new ComponentException(ex.getMessage(), ex);
       }
   }
}
