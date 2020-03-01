package me.legrange.services.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.services.logging.WithLogging;

import static java.lang.String.format;

/**
 * @author gideon
 */
public final class PostgresqlComponent extends JdbcComponent<Service, PostgresqlConfig> implements WithLogging {

    public PostgresqlComponent(Service service) {
        super(service);
    }
    @Override
    public String getName() {
        return "postgresql";
    }



}
