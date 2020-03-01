package me.legrange.services.jetty;

import java.util.EnumSet;
import java.util.StringJoiner;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import static me.legrange.log.Log.info;

/**
 * A component that adds Jetty HTTP functionality.
 *
 * @author gideon
 */
public class JettyComponent extends Component<Service, JettyConfig> {

    private Server server;
    private ServletContextHandler context;

    private final StringJoiner jerseyProviders = new StringJoiner(",");

    public JettyComponent(Service service) {
        super(service);
    }


    @Override
    public String getName() {
        return "jetty";
    }

    @Override
    public void start(JettyConfig config) throws ComponentException {
        try {
            context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.addFilter(ErrorFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
            server = new Server(config.getPort());
            server.setHandler(context);
            server.start();
            info("Started Jetty server on port %d", config.getPort());
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    /** Add a new endpoint with the given path and endpoint class. 
     * 
     * @param path The path 
     * @param endpoint The endpoint class
     */
    public void addEndpoint(String path, Class endpoint) throws ComponentException {
        ResourceConfig rc = new ResourceConfig(GsonJerseyProvider.class, endpoint);
        ServletHolder holder = new ServletHolder(new ServletContainer(rc));
        context.addServlet(holder, path + "/*");
        try {
            context.start();
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
        info("Added new endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
    }

    public void addProvider(Class provider) {
        jerseyProviders.add(provider.getCanonicalName());
        context.setInitParameter("jersey.config.server.provider.classnames", jerseyProviders.toString());
    }
}
