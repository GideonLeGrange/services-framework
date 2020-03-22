package me.legrange.services.helicopterorm;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jdbc.ConnectionPoolException;
import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.services.jdbc.WithJdbc;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmBuilder;
import net.legrange.orm.OrmException;

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
            orm = OrmBuilder.create(jdbc())
                    .setDialect(Orm.Dialect.valueOf(getComponent(JdbcComponent.class).getDialect()))
                    .build();
        } catch (ConnectionPoolException | OrmException ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
        catch (IllegalArgumentException ex) {
            throw new ComponentException("Invalid SQL dialect",ex);
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
