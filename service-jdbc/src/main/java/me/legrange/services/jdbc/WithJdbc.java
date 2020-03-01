package me.legrange.services.jdbc;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 *
 * @author matt-vm
 */
public interface WithJdbc extends WithComponent {

    default Supplier<Connection> jdbc() throws ComponentNotFoundException {
        return () -> getComponent(JdbcComponent.class).getConnection();
    }
}
