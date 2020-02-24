package me.legrange.services.postgresql;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithPostgresql extends WithComponent {

    default PostgresqlComponent postgresql() throws ComponentNotFoundException {
        return getComponent(PostgresqlComponent.class);
    }

}
