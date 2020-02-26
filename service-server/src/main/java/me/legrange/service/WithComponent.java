package me.legrange.service;

/**
 *
 * @author gideon
 */
public interface WithComponent {

//    <C extends Component> C requireComponent(Class<C> clazz) throws ServiceException;

    <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException;

}
