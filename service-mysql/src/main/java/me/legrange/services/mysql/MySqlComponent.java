package me.legrange.services.mysql;

import me.legrange.services.jdbc.JdbcComponent;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

/**
 *
 * @author gideon
 */
public final class MySqlComponent extends JdbcComponent<Service, MySqlConfig> implements WithLogging {

    public MySqlComponent(Service service) {
        super(service);
    }

    @Override
    public String getName() {
        return "mySql";
    }


    
}
