package me.legrange.services.rabbitmq;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithRabbitMq extends WithComponent {
    
    default RabbitMqComponent rabbitMq() throws ServiceException {
        return getComponent(RabbitMqComponent.class);
    }
    
}
