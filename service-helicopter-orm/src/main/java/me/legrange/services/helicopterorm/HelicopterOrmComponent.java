package me.legrange.services.helicopterorm;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.ServiceException;
import me.legrange.services.mysql.MySqlComponent;
import me.legrange.services.mysql.WithMySql;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmException;

/**
 *
 * @author matt-vm
 */
public class HelicopterOrmComponent extends Component<Service, HelicopterOrmConfig> implements WithMySql {

    private Orm orm;

    public HelicopterOrmComponent(Service service) {
        super(service);
    }

    @Override
    public void start(HelicopterOrmConfig config) throws ComponentException {
        try {
            orm = Orm.open(getComponent(MySqlComponent.class).getConnection(), Orm.Dialect.MYSQL);
        } catch (OrmException ex) {
            throw new ComponentException(ex.getMessage(), ex);
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
