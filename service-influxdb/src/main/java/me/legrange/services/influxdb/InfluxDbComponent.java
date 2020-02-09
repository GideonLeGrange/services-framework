package me.legrange.services.influxdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.influxdb.InfluxDbConfig;
import me.legrange.services.logging.WithLogging;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;

/**
 * @author gideon
 */
public class InfluxDbComponent extends Component<Service, InfluxDbConfig> implements WithLogging {

    private InfluxDB influxDB;

    public InfluxDbComponent(Service service) {
        super(service);
    }

    @Override
    public void start(InfluxDbConfig conf) throws ComponentException {
        influxDB = InfluxDBFactory.connect(conf.getUrl(), conf.getUsername(), conf.getPassword());
        boolean connected = false;
        while (!connected) {
            info("Connecting to InfluxDb server");
            Pong response = this.influxDB.ping();
            if (response.getVersion().equalsIgnoreCase("unknown")) {
                error("Error pinging InfluxDB server (%s)", response.toString());
            }
            if (!connected) {
                warning("Could not connect to InfluxDb server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    @Override
    public String getName() {
        return "InfluxDb";
    }

    public InfluxDB getConnection() {
        return influxDB;
    }


}
