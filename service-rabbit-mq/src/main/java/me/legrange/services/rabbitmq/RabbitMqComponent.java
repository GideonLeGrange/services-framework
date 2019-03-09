package me.legrange.services.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

/**
 *
 * @author gideon
 */
public final class RabbitMqComponent extends Component<Service, RabbitMqConfig> implements WithLogging {

    private Connection rabbitMq;
    private Channel channel;

    public RabbitMqComponent(Service service) {
        super(service);
    }

    @Override
    public void start(RabbitMqConfig conf) throws ComponentException {
        boolean connected = false;
        int retries = 0;
        while (!connected) {
            try {
                info("Connecting to RabbitMQ server");
                ConnectionFactory factory = new ConnectionFactory();
                factory.setUsername(conf.getUsername());
                factory.setPassword(conf.getPassword());
                factory.setVirtualHost(conf.getVirtualHost());
                factory.setHost(conf.getHostname());
                factory.setPort(conf.getPort());
                factory.setAutomaticRecoveryEnabled(true);
                rabbitMq = factory.newConnection();
                rabbitMq.addShutdownListener((ShutdownSignalException signal) -> {
                    warning("RabbitMq shut down. Reason: %s", signal.getMessage());
                });
                channel = rabbitMq.createChannel();
                connected = true;
                info("Connected to RabbitMQ server");
            } catch (IOException ex) {
                error(ex, "Error connecting to RabbitMQ server: %s", ex.getMessage());
            } catch (TimeoutException ex) {
                error(ex, "Timeout connecting to RabbitMQ server: %s", ex.getMessage());
            }
            if (!connected) {
                warning("Could not connect to RabbitMQ server. Retrying in %d seconds", conf.getRetryTime());
                try {
                    retries++;
                    if (retries > 3) {
                    }
                    TimeUnit.SECONDS.sleep(conf.getRetryTime());
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String getName() {
        return "rabbitMq";
    }


}
