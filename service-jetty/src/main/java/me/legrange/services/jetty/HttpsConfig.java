package me.legrange.services.jetty;

import javax.validation.constraints.NotNull;

public class HttpsConfig extends HttpConfig {

    public enum KeyStore {
        KEY_STORE,
        LETS_ENCRYPT;
    }

    @NotNull(message = "The key store must be specified")
    private KeyStore keyStore;
    private String alias;

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
