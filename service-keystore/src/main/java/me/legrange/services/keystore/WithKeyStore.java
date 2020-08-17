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

    default KeyStoreComponent keyStore() throws ComponentNotFoundException {
        return getComponent(KeyStoreComponent.class);
    }

    default void storeCertificate(String alias, Certificate certificate) throws StoreException {
        getComponent(KeyStoreComponent.class).storeCertificate(alias, certificate);
    }

    default KeyStore getKeyStore() {
        return getComponent(KeyStoreComponent.class).getKeyStore();
    }

    default String getPassword() {
        return getComponent(KeyStoreComponent.class).getPassword();
    }

}
