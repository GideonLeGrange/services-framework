package me.legrange.services.jdbc;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class JdbcComponent<S extends Service<?>, C extends JdbcConfig> extends Component<S, C> implements WithLogging {

    private String dialect;
    private ConnectionPool pool;

    public JdbcComponent(S service) {
        super(service);
    }
    
    @Override
     public void start(JdbcConfig conf) throws ComponentException {
        dialect = conf.getDialect().toUpperCase();
        pool = new ConnectionPool(conf);
        var connected = false;
        var retries = 0;
        while (!connected) {
            try {
                if (retries > 0) {
                    warning("Retrying connection to SQL server - attempt %d of %d",
                            retries, conf.getRetryAttempts());
                }
                else {
                    info("Connecting to SQL server");
                }
                var connection = pool.getConnection();
                connection.close();
                connected = true;
            } catch (ConnectionPoolException | SQLException ex) {
                error("Error connecting to SQL server (%s)", ex.getMessage());
            }
            if (!connected) {
                retries ++;
                if (retries >= conf.getRetryAttempts()) {
                    throw new ComponentException(format("Could not connect to SQL server after %d attempts", retries));
                }
                warning("Could not connect to SQL server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    @Override
    public void stop() {
        pool.close();
    }

    @Override
    public String getName() {
        return "jdbc";
    }

    public final ConnectionPool getPool() {
        return pool;
    }

    public final String getDialect() {
        return dialect;
    }
}
