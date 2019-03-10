package me.legrange.services.mysql;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithMySql extends WithComponent {
    
    default MySqlComponent mysql() throws ServiceException { 
        return getComponent(MySqlComponent.class);
    }
       
}
