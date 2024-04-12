package me.legrange.services.letsencrypt;

public class LetsEncryptException extends Throwable {
    public LetsEncryptException(String message) {
        super(message);
    }

    public LetsEncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
