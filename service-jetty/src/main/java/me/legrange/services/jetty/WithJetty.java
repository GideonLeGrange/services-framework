package me.legrange.services.jetty;

import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithJetty extends WithComponent {

   default JettyComponent jetty() {
        return getComponent(JettyComponent.class);
    }

    
}
