package me.legrange.service;

/**
 *
 * @author gideon
 */
public interface WithComponent {

    <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException;

}
