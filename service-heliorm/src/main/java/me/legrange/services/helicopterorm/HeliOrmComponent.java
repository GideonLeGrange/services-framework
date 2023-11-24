package me.legrange.services.helicopterorm;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.UncaughtOrmException;
import com.heliorm.sql.SqlDriver;
import com.heliorm.sql.SqlOrmBuilder;
import com.heliorm.sql.mysql.MySqlDriver;
import com.heliorm.sql.postgres.PostgreSqlDriver;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jdbc.ConnectionPoolException;
import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.services.jdbc.WithJdbc;

import java.lang.reflect.Proxy;

import static java.lang.String.format;

/**
 * @author matt-vm
 */
public final class HeliOrmComponent extends Component<Service, HeliOrmConfig> implements WithJdbc {

    private OrmPool pool;

    public HeliOrmComponent(Service service) {
        super(service);
    }

    @Override
    public void start(HeliOrmConfig config) throws ComponentException {
        try {
            String dialect = getComponent(JdbcComponent.class).getDialect();
            Class<? extends SqlDriver> driverClass;
            switch (dialect) {
                case "MYSQL":
                    driverClass = MySqlDriver.class;
                    break;
                case "POSTGRESQL":
                    driverClass = PostgreSqlDriver.class;
                    break;
                default:
                    throw new ComponentException(format("Unsupported driver type '%s'", dialect));
            }
            pool = new OrmPool(jdbc(), driverClass, config);
        } catch (ConnectionPoolException | OrmException ex) {
            throw new ComponentException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ComponentException("Invalid SQL dialect", ex);
        }
    }

    @Override
    public void stop() {
        pool.close();
    }

    public Orm getInstance() {
        return (Orm) Proxy.newProxyInstance(HeliOrmComponent.class.getClassLoader(),
                new Class[]{Orm.class}, new ProxyOrm(pool));
    }

    @Override
    public String getName() {
        return "orm";
    }

}
