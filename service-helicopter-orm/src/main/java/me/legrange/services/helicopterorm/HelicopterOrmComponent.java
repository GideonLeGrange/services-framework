package me.legrange.services.helicopterorm;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmException;

/**
 *
 * @author matt-vm
 */
public class HelicopterOrmComponent extends Component<Service, HelicopterOrmConfig> {

    private HelicopterOrmConfig ormConfig;

    public HelicopterOrmComponent(Service service) {
        super(service);
    }

    @Override
    public void start(HelicopterOrmConfig config) throws ComponentException {
        this.ormConfig = config;
        try {
            //test the connection
            Orm testInstance = getInstance();
            if (testInstance == null) {
                throw new ComponentException("Could not get ORM instance");
            }
        } catch (Throwable ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    public Orm getInstance() throws OrmException {

        try {
            final String connectionString = ormConfig.getMysql().getUrl() + "?user=" + ormConfig.getMysql().getUsername() + "password=" + ormConfig.getMysql().getPassword();
            
            return Orm.open(DriverManager.getConnection(connectionString), Orm.Driver.MYSQL);
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getName() {
        return "orm";
    }

}
