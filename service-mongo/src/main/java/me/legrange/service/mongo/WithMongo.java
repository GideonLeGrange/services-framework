package me.legrange.service.mongo;

import com.mongodb.MongoClient;
import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithMongo extends WithComponent {

    default MongoClient mongo() throws ComponentNotFoundException {
        return getComponent(MongoComponent.class).getClient();
    }
}
