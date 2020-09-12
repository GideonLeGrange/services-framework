package me.legrange.services.jetty;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.keystore.WithKeyStore;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static me.legrange.log.Log.info;
import static me.legrange.log.Log.warning;

/**
 * A component that adds Jetty HTTP functionality.
 *
 * @author gideon
 */
public class JettyComponent extends Component<Service, JettyConfig> implements WithKeyStore {

    public enum Connector {
        HTTP,
        HTTPS;
    }

    private Server server;
    private JettyConfig config;
    private ServletContextHandler httpContext;
    private ServletContextHandler httpsContext;
    private boolean running = false;
    private final Set<Class> jerseyProviders = new HashSet();
    private final Map<String, Class> endpoints = new HashMap<>();

    public JettyComponent(Service service) {
        super(service);
    }


    @Override
    public String getName() {
        return "jetty";
    }

    @Override
    public void start(JettyConfig config) throws ComponentException {
        this.config = config;
        try {
            server = new Server();
            List<ServerConnector> connectors = new ArrayList();
            if (config.getHttp() != null) {
                connectors.add(makePlainConnector());
                httpContext = makeContext("http");

            }
            if (config.getHttps() != null) {
                connectors.add(makeSslConnector());
                ServerConnector serverConnector = makeSslConnector();
                httpsContext = makeContext("https");
            }
            if (connectors.isEmpty()) {
                throw new JettyException("Neither HTTP nor HTTP are configured. At least one needs to be configured");
            }
            server.setConnectors(connectors.toArray(new ServerConnector[]{}));
            HandlerCollection handlers = new HandlerCollection();
            if (httpContext != null) {
                handlers.addHandler(httpContext);
            }
            if (httpsContext != null) {
                handlers.addHandler(httpsContext);
            }
            server.setHandler(handlers);
            server.start();
            info("Started Jetty server on %s", connectors.stream().map(c -> c.getPort()).collect(Collectors.toList()));
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    public void addEndpoint(Connector connector, String path, Class endpoint) throws JettyException {
        switch (connector) {
            case HTTP:
                addEndpoint(httpContext, path, endpoint);
                break;
            case HTTPS:
                addEndpoint(httpsContext, path, endpoint);
                break;
            default:
                throw new JettyException(format("Unsupported endpoint connector '%s'. BUG!", connector));
        }
    }

    /**
     * Add a new endpoint with the given path and endpoint class.
     *
     * @param path     The path
     * @param endpoint The endpoint class
     */
    public void addEndpoint(String path, Class endpoint) throws JettyException {
        addEndpoint(Connector.HTTP, path, endpoint);
        addEndpoint(Connector.HTTPS, path, endpoint);
    }

    public void addProvider(Class provider) throws JettyException {
        jerseyProviders.add(provider);
        if (running) {
            info("Re-adding %d endpoint(s) because of provider change", endpoints.size());
            try {
                HandlerCollection handlers = new HandlerCollection();
                if (httpContext != null) {
                    httpContext = makeContext("http");
                    handlers.addHandler(httpContext);
                }
                if (httpsContext != null) {
                    httpsContext = makeContext("https");
                    handlers.addHandler(httpsContext);
                }
                server.stop();
                server.setHandler(handlers);
                server.start();
            } catch (Exception ex) {
                throw new JettyException(ex.getMessage(), ex);
            }
            for (Map.Entry<String, Class> pair : endpoints.entrySet()) {
                addEndpoint(pair.getKey(), pair.getValue());
            }
        }
    }

    private void addEndpoint(ServletContextHandler context, String path, Class endpoint) throws JettyException {
        ResourceConfig rc = new ResourceConfig(endpoint);
        checkForMessageBodyWriter();
        for (Class provider : jerseyProviders) {
            rc.register(provider);
        }
        ServletHolder holder = new ServletHolder(new ServletContainer(rc));
        context.addServlet(holder, path + "/*");
        try {
            context.start();
        } catch (Exception ex) {
            throw new JettyException(ex.getMessage(), ex);
        }
        if (endpoints.containsKey(path)) {
            info("Re-added endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
        } else {
            info("Added new endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
            endpoints.put(path, endpoint);
        }
        running = true;
    }

    /**
     * Check for a MessageBodyWriter
     */
    private void checkForMessageBodyWriter() throws JettyException {
        if (jerseyProviders.stream().noneMatch(p -> MessageBodyWriter.class.isAssignableFrom(p))) {
            addProvider(GsonJerseyProvider.class);
            warning("No MessageBodyWriters were registered for serialization. Added %s as safety net, but remember to register them using the addProvider methods", GsonJerseyProvider.class.getSimpleName());
        }
    }

    private ServletContextHandler makeContext(String name) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addFilter(ErrorFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        context.setVirtualHosts(new String[]{"@" + name});
        return context;
    }

    private ServerConnector makeSslConnector() throws ComponentException {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        https.setSecureScheme("https");
        https.setSendServerVersion(true);
        https.setSendDateHeader(false);
        https.setSecurePort(config.getHttps().getPort());

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStore(getKeyStore());
        sslContextFactory.setKeyStorePassword(getPassword());
        sslContextFactory.setKeyManagerPassword(getPassword());
        sslContextFactory.setTrustStore(getKeyStore());
        sslContextFactory.setTrustStorePassword(getPassword());
        String alias = config.getHttps().getAlias();
        if ((alias != null) && !alias.isEmpty()) {
            sslContextFactory.setCertAlias(alias);
        }
        ServerConnector connector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https));
        connector.setPort(config.getHttps().getPort());
        connector.setName("https");
        return connector;
    }

    private ServerConnector makePlainConnector() throws ComponentException {
        HttpConfiguration https = new HttpConfiguration();
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getHttp().getPort());
        connector.setName("http");
        return connector;
    }

}
