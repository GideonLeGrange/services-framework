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
     * @throws ComponentException Thrown if there is a problem starting the component
     */
    public abstract void start(C config) throws ComponentException;

    /** Stop the component. Implementations can override this to release resources,
     * close connections or do other kinds of cleanup.
     *
     * @throws ComponentException Throw if there is a proble stopping the component
     */
    public void stop() throws ComponentException {

    }

    /**
     * Get the name of the component. This is unique and is used to find the
     * component's configuration in the service config.
     *
     * @return The component's name.
     */
    public abstract String getName();

    /** Call to determine if the component requires configuration. By default this is true,
     * but if a component does not require any configuration, it can be overriden to return false
     * which will cause the system not to try to locate it's configuration.
     *
     */
    public boolean requiresConfig(){
        return true;
    }

    /**
     * Access the service that owns this component. Utility method to get access
     * to functionality required by the component.
     *
     * @return The service
     */
    protected final S service() {
        return service;
    }

    public final <C extends Component> C getComponent(Class<C> clazz) throws ComponentNotFoundException {
        return (C) service.getComponent(clazz);
    }

    protected final <C extends Component> C requireComponent(Class<C> clazz) throws ServiceException {
        return (C) service.requireComponent(clazz);
    }
}
