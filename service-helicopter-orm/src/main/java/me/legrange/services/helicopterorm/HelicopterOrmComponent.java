package me.legrange.services.helicopterorm;

import com.heliorm.Orm;
import com.heliorm.OrmBuilder;
import com.heliorm.OrmException;
import com.heliorm.driver.SqlDriver;
import com.heliorm.driver.mysql.MySqlDriver;
import com.heliorm.driver.postgresql.PostgreSqlDriver;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jdbc.ConnectionPoolException;
import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.services.jdbc.WithJdbc;

import static java.lang.String.format;

/**
 * @author matt-vm
 */
public final class HelicopterOrmComponent extends Component<Service, HelicopterOrmConfig> implements WithJdbc {

    private Orm orm;

    public HelicopterOrmComponent(Service service) {
        super(service);
    }

    @Override
    public void start(HelicopterOrmConfig config) throws ComponentException {
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
            orm = OrmBuilder.create(jdbc(), driverClass)
                    .setCreateMissingTables(config.isCreateMissingTables())
                    .build();
        } catch (ConnectionPoolException | OrmException ex) {
            throw new ComponentException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ComponentException("Invalid SQL dialect", ex);
        }
    }

    public Orm getInstance() {
        return orm;
    }

    @Override
    public String getName() {
        return "orm";
    }

}
