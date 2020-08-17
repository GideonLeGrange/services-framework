package me.legrange.services.keystore;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

import java.security.KeyStore;
import java.security.cert.Certificate;

/**
 *
 * @author gideon
 */
public interface WithKeyStore extends WithComponent {

    default KeyStore keyStore() throws ComponentNotFoundException {
        return getComponent(KeyStoreComponent.class).getKeyStore();
    }

    default void storeCertificate(String alias, Certificate certificate) throws StoreException {
        getComponent(KeyStoreComponent.class).storeCertificate(alias, certificate);
    }

}
