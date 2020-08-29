package me.legrange.services.keystore;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

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

    default void storeKey(String alias, PrivateKey key, List<? extends Certificate> chain) throws StoreException {
        getComponent(KeyStoreComponent.class).storeKey(alias, key, chain.toArray(new Certificate[]{}));
    }

    default KeyStore getKeyStore() {
        return getComponent(KeyStoreComponent.class).getKeyStore();
    }

    default String getPassword() {
        return getComponent(KeyStoreComponent.class).getPassword();
    }

}
