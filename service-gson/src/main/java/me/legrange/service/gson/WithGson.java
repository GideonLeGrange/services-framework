package me.legrange.service.gson;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithGson extends WithComponent {

    default GsonComponent gson() throws ServiceException {
        return getComponent(GsonComponent.class);
    }

}
