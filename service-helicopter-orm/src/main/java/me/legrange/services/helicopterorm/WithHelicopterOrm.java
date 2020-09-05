package me.legrange.services.helicopterorm;

import com.heliorm.Orm;
import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithHelicopterOrm extends WithComponent {

    default Orm orm() throws ComponentNotFoundException {
        return getComponent(HelicopterOrmComponent.class).getInstance();
    }
}
