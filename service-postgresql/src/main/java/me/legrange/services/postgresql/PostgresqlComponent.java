package me.legrange.services.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import static java.lang.String.format;

/**
 * @author gideon
 */
public class PostgresqlComponent extends Component<Service, PostgresqlConfig> implements WithLogging {
    private ConnectionPool pool;

    public PostgresqlComponent(Service service) {
        super(service);
    }

    @Override
    public void start(PostgresqlConfig conf) throws ComponentException {
        pool = new ConnectionPool(conf);
        boolean connected = false;
        while (!connected) {
            try {
                info("Connecting to Postgresql server");
                Connection connection = pool.getConnection();
                connected = true;
            } catch (SQLException ex) {
                error(ex, "Error connecting to Postgresql server: %s", ex.getMessage());
            }
            if (!connected) {
                warning("Could not connect to Postgresql server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    @Override
    public String getName() {
        return "postgresql";
    }

    public Connection getConnection() throws ConnectionPoolException {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new ConnectionPoolException(format("Error obtaining MySQL connection from pool (%s)", e.getMessage()), e);
        }
    }


}
