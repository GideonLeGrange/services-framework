package me.legrange.service;

/**
 *
 * @author gideon
 */
public abstract interface WithComponent {

    <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException;

}
