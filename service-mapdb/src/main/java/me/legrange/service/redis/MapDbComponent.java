package me.legrange.service.redis;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * A component that provides access to MapDB.
 */
public class MapDbComponent extends Component<Service, MapDbConfig> {

    private DB db;

    public MapDbComponent(Service service) {
        super(service);
    }

    @Override
    public void start(MapDbConfig config) throws ComponentException {
        DBMaker.Maker maker = DBMaker.fileDB(config.getDatabaseFile());
        if (config.isMemoryMapped()) {
            maker = maker.fileMmapEnable();
        }
        maker.closeOnJvmShutdown().make();
    }

    @Override
    public String getName() {
        return "mapDb";
    }

    DB mapDb() {
        return db;
    }
}
