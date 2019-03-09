package za.co.adept.services.mysql;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.monitor.MonitoredComponent;
import me.legrange.service.monitor.State;
import me.legrange.service.monitor.Status;

/**
 *
 * @author gideon
 * @param <S>
 */
public class MySqlComponent<S extends Service> extends MonitoredComponent<S, MySqlConfig> {
    
    private Connection connection;

    public MySqlComponent(S service) {
        super(service);
    }
    
    
    @Override
     public void start(MySqlConfig conf) throws ComponentException {
        boolean connected = false;
        while (!connected) {
            try{
                info("Connecting to MySQL server");
                connection = DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());
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
        monitor("mysql", () -> getState());
    }

    @Override
    public String getName() {
        return "mySql";
    }
    
    public Connection getConnection() {
        return connection;
    }

    private State getState() { 
        try {
            if (connection.isValid(3)) {
                return new State(Status.OK, "MySQL connection is live", Collections.EMPTY_LIST);
            }
        } catch (SQLException ex) {
                return new State(Status.ERROR, format("MySQL error: %s", ex.getMessage()), Collections.EMPTY_LIST);
        }
        return new State(Status.ERROR, "MySQL did not respond in 3 seconds", Collections.EMPTY_LIST);
    }
    
}
