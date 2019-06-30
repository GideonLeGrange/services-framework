package me.legrange.service.retrofit.auth;

import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author matt-vm
 */
public class AuthInterceptor implements Interceptor {

    private final String authHeaderValue;

    /**
     * Token based
     *
     * @param token
     */
    public AuthInterceptor(String token) {
        this.authHeaderValue = token;
    }

    /**
     * Basic auth
     *
     * @param user
     * @param password
     */
    public AuthInterceptor(String user, String password) {
        this.authHeaderValue = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", authHeaderValue).build();
        return chain.proceed(authenticatedRequest);
    }

}
