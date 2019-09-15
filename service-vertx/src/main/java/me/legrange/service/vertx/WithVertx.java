package me.legrange.service.vertx;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithVertx extends WithComponent {

    default VertxComponent vertx() throws ServiceException {
        return getComponent(VertxComponent.class);
    }
}
