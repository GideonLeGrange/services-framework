package me.legrange.services.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public final class MySqlComponent extends JdbcComponent<Service, MySqlConfig> implements WithLogging {

    public MySqlComponent(Service service) {
        super(service);
    }

    @Override
    public String getName() {
        return "mySql";
    }


    
}
