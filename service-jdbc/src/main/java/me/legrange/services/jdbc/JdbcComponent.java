package me.legrange.services.jdbc;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import java.sql.Connection;
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
            try{
                info("Connecting to SQL server");
                Connection connection = pool.getConnection();
                connected = true;
            } catch (ConnectionPoolException ex) {
                error(ex);
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
    public void stop() throws ComponentException {
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
