package me.legrange.service.retrofit;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author matt-vm
 */
public interface WithRetrofit extends WithComponent {

    default RetrofitComponent retrofit() throws ComponentNotFoundException {
        return getComponent(RetrofitComponent.class);
    }
}
