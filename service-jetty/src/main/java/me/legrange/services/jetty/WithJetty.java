package me.legrange.services.jetty;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;


/**
 *
 * @author gideon
 */
public interface WithJetty extends WithComponent {

   default JettyComponent jetty() throws ComponentNotFoundException {
        return getComponent(JettyComponent.class);
    }

    
}
