package me.legrange.service.jetty;

import java.util.StringJoiner;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import static za.co.adept.log.Log.info;

/**
 *
 * @author gideon
 * @param <S>
 */
public class JettyComponent<S extends Service> extends Component<S, JettyConfig> {

    private Server server;
    private ServletContextHandler context;
    private ServletHolder serveletHolder;
    private final StringJoiner jerseyProviders = new StringJoiner(",");

    public JettyComponent(S service) {
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
