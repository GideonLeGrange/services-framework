package me.legrange.service.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static java.lang.String.format;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.monitor.MonitoredComponent;
import me.legrange.service.monitor.State;
import me.legrange.service.monitor.Status;

/**
 *
 * @author gideon
 */
public final class RabbitMqComponent extends MonitoredComponent<Service, RabbitMqConfig> {

    private Connection rabbitMq;
    private Channel channel;
    private State state;

    public RabbitMqComponent(Service service) {
        super(service);
    }

    @Override
    public void start(RabbitMqConfig conf) throws ComponentException {
        boolean connected = false;
        monitor("rabbitMq-connection", () -> getState());
        state = new State(Status.WARNING, "RabbitMQ starting up", Collections.EMPTY_LIST);
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
                    state = new State(Status.ERROR, "RabbitMQ was disconnected", Collections.EMPTY_LIST);
                });
                channel = rabbitMq.createChannel();
                connected = true;
                state = new State(Status.OK, "RabbitMQ connected", Collections.EMPTY_LIST);
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
                        state = new State(Status.ERROR, format("RabbitMQ could not connect in %d tries", retries), Collections.EMPTY_LIST);
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

    private State getState() {
        if (state != null) {
            return state;
        }
        return new State(Status.WARNING, "No status information found", Collections.EMPTY_LIST);
    }

}
