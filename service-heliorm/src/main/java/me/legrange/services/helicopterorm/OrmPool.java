package me.legrange.services.helicopterorm;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.sql.SqlDriver;
import com.heliorm.sql.SqlOrmBuilder;
import me.legrange.services.jdbc.ConnectionPool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static me.legrange.log.Log.debug;

final class OrmPool {
    private final int MAX_POOL_SIZE = 16;
    private final int MIN_POOL_SIZE = 4;
    private final List<Orm> available = new LinkedList<>();
    private final Set<Orm> inUse = new HashSet<>();

    private final HeliOrmConfig config;
    private final ConnectionPool pool;
    private final Class<? extends SqlDriver> driverClass;

    OrmPool(ConnectionPool pool, Class<? extends SqlDriver> driverClass, HeliOrmConfig config) throws OrmException {
        this.pool = pool;
        this.config = config;
        this.driverClass = driverClass;
        for (var i = 0; i < MIN_POOL_SIZE; ++i) {
            available.add(createOrm());
        }
    }

    synchronized Orm issue() throws OrmException {
        if (available.isEmpty()) {
            if (inUse.size() >= MAX_POOL_SIZE) {
                throw new OrmException(format("ORM pool (%s) exhausted", MAX_POOL_SIZE));
            }
            available.add(createOrm());
        }
        var orm = available.removeFirst();
        inUse.add(orm);
        debug(() -> format("pool[issue]: available = %d, in use = %d", available.size(), inUse.size()));
        return orm;
    }

    void close() {

    }

    synchronized void release(Orm orm) {
        inUse.remove(orm);
        available.add(orm);
        while (available.size() > MIN_POOL_SIZE) {
           available.removeFirst().close();
        }
        debug(() -> format("pool[release]: available = %d, in use = %d", available.size(), inUse.size()));
    }

    private Orm createOrm() throws OrmException {
        return SqlOrmBuilder.create(pool::getConnection, driverClass)
                .setCreateMissingTables(config.isCreateMissingTables())
                .setRollbackOnUncommittedClose(config.isRollbackOnUncommittedClose())
                .setUseUnionAll(config.isUseUnionAll())
                .build();
    }

}
