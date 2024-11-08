package me.legrange.services.jdbc;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
            try {
                info("Connecting to SQL server");
                var connection = pool.getConnection();
                connection.close();
                connected = true;
            } catch (ConnectionPoolException | SQLException ex) {
                error(ex);
            }
            if (!connected) {
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
