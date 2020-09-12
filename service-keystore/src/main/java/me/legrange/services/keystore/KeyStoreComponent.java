package me.legrange.services.keystore;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * A component that manages a key store for use by TLS.
 */
public final class KeyStoreComponent extends Component<Service, KeyStoreConfig> implements WithLogging {

    private KeyStoreConfig config;
    private KeyStore keyStore;
    private List<Consumer<String>> listeners = new ArrayList<>();

    public KeyStoreComponent(Service service) {
        super(service);
    }

    @Override
    public void start(KeyStoreConfig config) throws ComponentException {
        this.config = config;
        if (haveKeyStore()) {
            loadKeyStore();
        } else {
            createKeyStore();
        }
    }

    @Override
    public String getName() {
        return "keyStore";
    }

    public void storeKey(String alias, PrivateKey key, Certificate[] chain) throws StoreException {
        try {
            keyStore.setKeyEntry(alias, key, getPassword().toCharArray(), chain);
            saveKeyStore();
            updateListeners(alias);
        } catch (KeyStoreException ex) {
            throw new StoreException(format("Error storing key '%s' (%s)", alias, ex.getMessage()), ex);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public String getPassword() {
        return config.getPassword();
    }

    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    private boolean haveKeyStore() {
        return new File(config.getFileName()).exists();
    }

    private void createKeyStore() throws StoreException {
        keyStore = null;
        try (FileOutputStream out = new FileOutputStream(config.getFileName())) {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, config.getPassword().toCharArray());
            keyStore.store(out, config.getPassword().toCharArray());
            info("Created new key store %s", config.getFileName());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new StoreException(format("Cannot create key store %s (%s)", config.getFileName(), e.getMessage()), e);
        }
    }

    private void loadKeyStore() throws StoreException {
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(config.getFileName()), config.getPassword().toCharArray());
            info("Loaded existing key store from %s", config.getFileName());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new StoreException(format("Error loading key store %s (%s)", config.getFileName(), e.getMessage()), e);
        }
    }

    private void saveKeyStore() throws StoreException {
        try (FileOutputStream out = new FileOutputStream(config.getFileName())) {
            keyStore.store(out, config.getPassword().toCharArray());
            info("Updated key store %s", config.getFileName());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new StoreException(format("Cannot update key store %s (%s)", config.getFileName(), e.getMessage()), e);
        }
    }

    public boolean hasAlias(String alias) throws StoreException {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new StoreException(format("Cannot read alias %s from key store (%s)", alias, e.getMessage()), e);
        }

    }

    private void updateListeners(String alias) {
        for (Consumer<String> listener : listeners) {
            try {
                listener.accept(alias);
            }
            catch (Exception ex) {
                error(ex, "Uncaught error in key store listener (%s)", ex.getMessage());
            }
        }
    }

}
