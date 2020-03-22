package me.legrange.services.jdbc;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class JdbcComponent<S extends Service, C extends JdbcConfig> extends Component<S, C> implements WithLogging {

    private String dialect;
    private ConnectionPool pool;

    public JdbcComponent(S service) {
        super(service);
    }
    
    @Override
     public void start(JdbcConfig conf) throws ComponentException {
        dialect = conf.getDialect().toUpperCase();
        pool = new ConnectionPool(conf);
        boolean connected = false;
        while (!connected) {
            try{
                info("Connecting to SQL server");
                Connection connection = pool.getConnection();
                connected = true;
            } catch (SQLException ex) {
                error(ex, "Error connecting to SQL server: %s", ex.getMessage());
            }
            if (!connected) {
                warning("Could not connect to SQL server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    @Override
    public String getName() {
        return "jdbc";
    }

    public final Connection getConnection() throws ConnectionPoolException {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new ConnectionPoolException(format("Error obtaining SQL connection from pool (%s)", e.getMessage()),e);
        }
    }

    public final String getDialect() {
        return dialect;
    }
}
