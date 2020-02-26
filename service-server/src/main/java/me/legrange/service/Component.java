package me.legrange.service;

/**
 * Extended by packaged service components to gain access to useful
 * functionality in the service.
 *
 * @param <C> Type of the configuration class
 * @param <S> Type of the service required by this component
 * @author gideon
 */
public abstract class Component<S extends Service, C> {

    private final S service;

    /**
     * Create a component associated with the given service.
     *
     * @param service The service of which this component is a part of.
     */
    protected Component(S service) {
        this.service = service;
    }

    /**
     * Start the component. Implementations need to do anything here they need
     * done before the component's functionality is available.
     *
     * @param config The component's configuration.
     * @throws ComponentException
     */
    public abstract void start(C config) throws ComponentException;

    /**
     * Get the name of the component. This is unique and is used to find the
     * component's configuration in the service config.
     *
     * @return The component's name.
     */
    public abstract String getName();

    /**
     * Access the service that owns this component. Utility method to get access
     * to functionality required by the component.
     *
     * @return The service
     */
    protected S service() {
        return service;
    }

    public final <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException {
        return (C) service.getComponent(clazz);
    }

    protected <C extends Component> C requireComponent(Class<C> clazz) throws ServiceException {
        return (C) service.requireComponent(clazz);
    }
}
