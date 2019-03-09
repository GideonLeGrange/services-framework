package me.legrange.services.jetty;

import java.util.EnumSet;
import java.util.StringJoiner;
import javax.servlet.DispatcherType;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * A component that adds Jetty HTTP functionality. 
 * @author gideon
 */
public class JettyComponent extends Component<Service, JettyConfig> implements WithLogging {

    private Server server;
    private ServletContextHandler context;
    private ServletHolder serveletHolder;
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
            context.addFilter(ErrorFilter.class, "/*",EnumSet.allOf(DispatcherType.class));
            server = new Server();
            server.setHandler(context);
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(config.getPort());
            serveletHolder = context.addServlet(ServletContainer.class, "/*");
            server.setConnectors(new Connector[]{connector});
            server.start();
            info("Jetty server started on port %d", config.getPort());
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    public void addRestEndpoint(Class endpoint) {
        jerseyProviders.add(endpoint.getCanonicalName());
        updateServlet();
    }
    
    public void addProvider(Class provider){
        jerseyProviders.add(provider.getCanonicalName());
        updateServlet();
    }

    private void updateServlet() {
        serveletHolder.setInitParameter("jersey.config.server.provider.classnames", jerseyProviders.toString());
    }
}
