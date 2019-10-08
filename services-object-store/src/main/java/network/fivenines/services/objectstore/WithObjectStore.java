package network.fivenines.services.objectstore;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithObjectStore extends WithComponent {

    default ObjectStoreComponent objectStore() throws ServiceException {
        return getComponent(ObjectStoreComponent.class);
    }

}
