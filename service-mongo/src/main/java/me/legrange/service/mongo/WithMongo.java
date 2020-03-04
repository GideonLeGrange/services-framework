package me.legrange.service.mongo;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithMongo extends WithComponent {

    default MongoComponent mongo() throws ComponentNotFoundException {
        return getComponent(MongoComponent.class);
    }
}
