package me.legrange.services.mysql;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithMysql extends WithComponent {
    
    default MySqlComponent mysql() throws ServiceException { 
        return getComponent(MySqlComponent.class);
    }
       
}
