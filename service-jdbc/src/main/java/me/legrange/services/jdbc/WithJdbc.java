package me.legrange.services.jdbc;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithJdbc extends WithComponent {

    default ConnectionPool jdbc() throws ComponentNotFoundException {
        return getComponent(JdbcComponent.class).getPool();
    }
}
