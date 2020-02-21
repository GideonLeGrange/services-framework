package me.legrange.services.helicopterorm;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;
import net.legrange.orm.Orm;

/**
 *
 * @author matt-vm
 */
public interface WithHelicopterOrm extends WithComponent {

    default Orm orm() throws ComponentNotFoundException {
        return getComponent(HelicopterOrmComponent.class).getInstance();
    }
}
