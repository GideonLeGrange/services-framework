package me.legrange.services.jetty;

import jakarta.servlet.DispatcherType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static me.legrange.log.Log.debug;
import static me.legrange.log.Log.info;
import static me.legrange.log.Log.warning;

/**
 * A component that adds Jetty HTTP functionality.
 *
 * @author gideon
 */
public class JettyComponent extends Component<Service<?>, JettyConfig> {

    private Server server;
    private ServletContextHandler context;
    private boolean running = false;
    private final List<Object> jerseyProviders = new ArrayList<>();
    private final Map<String, Set<Class<?>>> endpoints = new HashMap<>();
    private JettyConfig config;

    public JettyComponent(Service<?> service) {
        super(service);
    }


    @Override
    public String getName() {
        return "jetty";
    }

    @Override
    public void start(JettyConfig config) throws ComponentException {
        this.config = config;
        if (config.isEnabled()) {
            try {
                context = makeContext();
                server = new Server();
                server.setHandler(gzip(context));
                var connectors = new ArrayList<ServerConnector>();
                connectors.add(setupHttp(server, config));
                if (config.getHttps() != null) {
                    connectors.add(setupHttps(server, config.getHttps()));
                    info("HTTPS enabled on port %d", config.getHttps().getPort());
                }
                server.setConnectors(connectors.toArray(new Connector[]{}));
                server.start();
                info("Started Jetty server on port %d", config.getPort());
            } catch (Exception ex) {
                throw new ComponentException(ex.getMessage(), ex);
            }
        } else {
            warning("Jetty server disabled");
        }
    }

    @Override
    public void stop() throws ComponentException {
        if (config.isEnabled()) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new ComponentException(e.getMessage(), e);
            }
        }
    }

    public void addEndpoints(String path, Set<Class<?>> endpoints) throws ComponentException {
        ResourceConfig rc = new ResourceConfig(endpoints);

        rc.addProperties(Collections.singletonMap("jersey.config.server.wadl.disableWadl", "true"));
        rc.addProperties(Collections.singletonMap(ServerProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0));

        for (Object provider : jerseyProviders) {
            rc.register(provider);
        }
        ServletHolder holder = new ServletHolder(new ServletContainer(rc));
        context.addServlet(holder, path + "/*");
        try {
            context.start();
        } catch (Exception ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
        if (this.endpoints.containsKey(path)) {
            info("Re-added %d endpoints on path %s", endpoints.size(), path);
        } else {
            info("Added %d new endpoint of type  on path %s", endpoints.size(), path);
            this.endpoints.put(path, endpoints);
        }
        running = true;
        checkForMessageBodyWriter();
    }

    /**
     * Add a new endpoint with the given path and endpoint class.
     *
     * @param path     The path
     * @param endpoint The endpoint class
     */
    public void addEndpoint(String path, Class<?> endpoint) throws ComponentException {
        ResourceConfig rc = new ResourceConfig(endpoint);
        checkForMessageBodyWriter();
        for (Object provider : jerseyProviders) {
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
        } else {
            info("Added new endpoint of type '%s' on '%s'", endpoint.getSimpleName(), path);
            endpoints.put(path, Collections.singleton(endpoint));
        }
        running = true;
    }

    public void addProvider(Class<?> provider) throws ComponentException {
        try {
            addProvider(provider.getConstructor().newInstance());
        } catch (NoSuchMethodException e) {
            throw new ComponentException(format("No default constructor for provider class %s. Either pass an instance or a provider with a default constructor", provider.getSimpleName()), e);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ComponentException(format("Error creating provider from class %s (%s)", provider.getSimpleName(), e.getMessage()), e);
        }
    }

    public void addProvider(Object provider) throws ComponentException {
        if (provider instanceof MessageBodyWriter) {
            Optional<? extends MessageBodyWriter<?>> mbr = findMessageBodyWriter();
            if (mbr.isPresent()) {
                if (mbr.get() instanceof GsonJerseyProvider) {
                    jerseyProviders.remove(mbr.get());
                    warning("Replacing message body writer '%s' with '%s'",
                            mbr.get().getClass().getSimpleName(),
                            provider.getClass().getName());
                }
            }
        }
        jerseyProviders.add(provider);
        if (running) {
            info("Re-adding %d endpoint(s) because of provider change", endpoints.size());
            context = makeContext();
            try {
                server.stop();
                server.setHandler(gzip(context));
                server.start();
            } catch (Exception ex) {
                throw new ComponentException(ex.getMessage(), ex);
            }
            for (Map.Entry<String, Set<Class<?>>> pair : endpoints.entrySet()) {
                addEndpoints(pair.getKey(), pair.getValue());
            }
        }
    }

    public void addCertificate(String alias, Certificate cert) throws ComponentException {
        try {
            var keyStore = validateKeyStore(config.getHttps());
            keyStore.setCertificateEntry(alias, cert);
            keyStore.store(new FileOutputStream(config.getHttps().getKeystorePath()), config.getHttps().getKeystorePassword().toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new ComponentException(format("Error adding certificate to key store (%s)",  e.getMessage()),e);
        }
    }

    /**
     * Check for a MessageBodyWriter
     */
    private void checkForMessageBodyWriter() throws ComponentException {
        if (findMessageBodyWriter().isEmpty()) {
            throw new ComponentException("No MessageBodyWriters were registered for serialization. Remember to register them using the addProvider methods");
        }
    }

    private Optional<? extends MessageBodyWriter<?>> findMessageBodyWriter() {
        return jerseyProviders.stream()
                .filter(MessageBodyWriter.class::isInstance)
                .map(p -> (MessageBodyWriter<?>) p)
                .findFirst();
    }

    private ServletContextHandler makeContext() {
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        if (config.isStandardErrorFilter()) {
            context.addFilter(ErrorFilter.class, "/*", EnumSet.of(DispatcherType.ERROR));
        }
        return context;
    }

    private Handler gzip(Handler context) {
        if (config.isEnableGzip()) {
            GzipHandler handler = new GzipHandler();
            handler.setIncludedMethods("PUT", "POST", "GET");
            handler.setInflateBufferSize(2048);
            handler.setHandler(context);
            return handler;
        }
        return context;
    }

    private ServerConnector setupHttps(Server server, HttpsConfig config) throws ComponentException {
        // HTTPS configuration
        var https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        // Configuring SSL
        var sslContextFactory = new SslContextFactory.Server();
        // Defining keystore path and passwords
    //    sslContextFactory.setKeyStore(validateKeyStore(config));
        sslContextFactory.setKeyStorePath("/tmp/keystore");
        sslContextFactory.setKeyStorePassword("suckmyballsmate");
        // Configuring the connector
        var sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setPort(config.getPort());
        debug("Setup HTTPS on port %d", config.getPort());
        return sslConnector;
    }

    private ServerConnector setupHttp(Server server, JettyConfig config) {
        // HTTP Configuration
        var http = new HttpConfiguration();
        http.addCustomizer(new SecureRequestCustomizer());
        var connector = new ServerConnector(server);
        connector.addConnectionFactory(new HttpConnectionFactory(http));
        // Setting HTTP port
        connector.setPort(config.getPort());
        debug("Setup HTTP on port %d", config.getPort());
        return connector;
    }

    private KeyStore validateKeyStore(HttpsConfig config) throws ComponentException {
        var file = new File(config.getKeystorePath());
        if (!file.exists()) {
            try {
                var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.store(new FileOutputStream(file), config.getKeystorePassword().toCharArray());
                return keyStore;
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                throw new ComponentException(format("Error creating keystore (%s)", e.getMessage()), e);
            }
        } else {
            try {

                var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(new FileInputStream(config.getKeystorePath()), config.getKeystorePassword().toCharArray());
                return keyStore;
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                throw new ComponentException(format("Error loading keystore (%s)", e.getMessage()), e);
            }
        }
    }

}
