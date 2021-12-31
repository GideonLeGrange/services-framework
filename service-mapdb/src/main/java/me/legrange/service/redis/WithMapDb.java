package me.legrange.service.redis;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;
import org.mapdb.DB;

public interface WithMapDb extends WithComponent {

    default DB mapDb() throws ComponentNotFoundException {
        return getComponent(MapDbComponent.class).mapDb();
    }
}
