package me.legrange.service.rabbitmq;

import me.legrange.service.WithComponent;


/**
 *
 * @author gideon
 */
public interface WithRabbitMq extends WithComponent {
    
    default RabbitMqComponent rabbitMq() {
        return getComponent(RabbitMqComponent.class);
    }
    
}
