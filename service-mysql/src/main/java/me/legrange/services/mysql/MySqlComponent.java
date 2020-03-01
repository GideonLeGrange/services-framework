package me.legrange.services.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class MySqlComponent extends Component<Service, MySqlConfig> implements WithLogging {
    
    private ConnectionPool pool;

    public MySqlComponent(Service service) {
        super(service);
    }
    
    @Override
     public void start(MySqlConfig conf) throws ComponentException {
        pool = new ConnectionPool(conf);
        boolean connected = false;
        while (!connected) {
            try{
                info("Connecting to MySQL server");
                Connection connection = pool.getConnection();
                connected = true;
            } catch (SQLException ex) {
                error(ex, "Error connecting to MySQL server: %s", ex.getMessage());
            }
            if (!connected) {
                warning("Could not connect to MySQL server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    @Override
    public String getName() {
        return "mySql";
    }
    
    public Connection getConnection() throws ConnectionPoolException {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new ConnectionPoolException(format("Error obtaining MySQL connection from pool (%s)", e.getMessage()),e);
        }
    }

    
}
