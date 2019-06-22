package me.legrange.services.helicopterorm;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithHelicopterOrm extends WithComponent {

    default OrmComponent orm() throws ServiceException {
        return getComponent(OrmComponent.class);
    }
}
