package me.legrange.services.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.ws.rs.ext.MessageBodyWriter;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import static java.lang.String.format;
import static me.legrange.log.Log.info;
import static me.legrange.log.Log.warning;

/**
 * A component that adds Jetty HTTP functionality.
 *
 * @author gideon
 */
public class JettyComponent extends Component<Service, JettyConfig> {

    private Server server;
    private JettyConfig config;
    private ServletContextHandler context;
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
            context = makeContext();
            if (config.getSsl() != null)  {
                server = new Server();
                ServerConnector sslConnector = makeSslConnector();
                server.setConnectors(new ServerConnector[]{sslConnector});
            }
            else {
                server = new Server(config.getPort());
            }
            server.setHandler(context);
            server.start();
            info("Started Jetty server on port %d", config.getPort());
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    /**
     * Add a new endpoint with the given path and endpoint class.
     *
     * @param path     The path
     * @param endpoint The endpoint class
     */
    public void addEndpoint(String path, Class endpoint) throws ComponentException {
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
            throw new ComponentException(ex.getMessage(), ex);
        }
        if (endpoints.containsKey(path)) {
            info("Re-added endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
        }
        else {
            info("Added new endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
            endpoints.put(path, endpoint);
        }
        running = true;
    }

    public void addProvider(Class provider) throws ComponentException {
        jerseyProviders.add(provider);
        if (running) {
            info("Re-adding %d endpoint(s) because of provider change", endpoints.size());
            context = makeContext();
            try {
                server.stop();
                server.setHandler(context);
                server.start();
            }
            catch (Exception ex) {
                throw new ComponentException(ex.getMessage(), ex);
            }
            for (Map.Entry<String, Class> pair : endpoints.entrySet()) {
                addEndpoint(pair.getKey(), pair.getValue());
            }
        }
    }

    /**
     * Check for a MessageBodyWriter
     */
    private void checkForMessageBodyWriter() throws ComponentException {
        if (jerseyProviders.stream().noneMatch(p -> MessageBodyWriter.class.isAssignableFrom(p))) {
            addProvider(GsonJerseyProvider.class);
            warning("No MessageBodyWriters were registered for serialization. Added %s as safety net, but remember to register them using the addProvider methods", GsonJerseyProvider.class.getSimpleName());
        }
    }

    private ServletContextHandler makeContext() {
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addFilter(ErrorFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        return context;
    }

    private ServerConnector makeSslConnector() throws ComponentException {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        if (!keyStoreExists()) {
            createKeyStore();
        }
        sslContextFactory.setKeyStorePath(config.getSsl().getKeyStoreFile());
        sslContextFactory.setKeyStorePassword(config.getSsl().getKeyStorePassword());
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(config.getPort());
        return sslConnector;
    }

    private boolean keyStoreExists() {
        return new File(config.getSsl().getKeyStoreFile()).exists();
    }

    private void createKeyStore() throws ComponentException {
        KeyStore keystore = null;
        try (FileOutputStream out = new FileOutputStream(config.getSsl().getKeyStoreFile())){
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
            keystore.store(out, config.getSsl().getKeyStorePassword().toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new  ComponentException(format("Cannot create key store (%s)", e.getMessage()),e);
        }
    }
}
