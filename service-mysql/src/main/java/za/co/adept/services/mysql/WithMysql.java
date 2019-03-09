package za.co.adept.services.mysql;

import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithMysql extends WithComponent {
    
    default MySqlComponent mysql() { 
        return getComponent(MySqlComponent.class);
    }
       
}
