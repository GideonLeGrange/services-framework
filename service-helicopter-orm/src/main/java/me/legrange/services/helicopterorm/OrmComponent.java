package me.legrange.services.helicopterorm;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.ServiceException;
import me.legrange.services.mysql.MySqlComponent;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmException;

/**
 *
 * @author matt-vm
 */
public class OrmComponent extends Component<Service, OrmConfig> {

    private OrmConfig ormConfig;
    private Orm orm;

    public OrmComponent(Service service) {
        super(service);
    }

    @Override
    public void start(OrmConfig config) throws ComponentException {
        this.ormConfig = config;
        try {
            orm = Orm.open(getComponent(MySqlComponent.class).getConnection(), Orm.Driver.MYSQL);
        } catch (ServiceException | OrmException ex) {
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
