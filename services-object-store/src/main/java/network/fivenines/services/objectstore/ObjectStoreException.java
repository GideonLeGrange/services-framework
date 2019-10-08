package network.fivenines.services.objectstore;

import me.legrange.service.ComponentException;

/**
 *
 * @author gideon
 */
public class ObjectStoreException extends ComponentException {

    public ObjectStoreException(String message) {
        super(message);
    }

    public ObjectStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
