package me.legrange.services.influxdb;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithInfluxDb extends WithComponent {

    default InfluxDbComponent influx() throws ComponentNotFoundException {
        return getComponent(InfluxDbComponent.class);
    }

}
