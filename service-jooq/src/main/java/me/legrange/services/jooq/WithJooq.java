package me.legrange.services.jooq;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt
 */
public interface WithJooq extends WithComponent{
    default JooqComponent jooq() throws ComponentNotFoundException {
        return getComponent(JooqComponent.class);
    }
}
