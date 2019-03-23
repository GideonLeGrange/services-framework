package me.legrange.services.jooq;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt
 */
public interface WithJooq extends WithComponent{
    default JooqComponent jooq() throws ServiceException { 
        return getComponent(JooqComponent.class);
    }
}
