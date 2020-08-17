package me.legrange.services.jetty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 * @author gideon
 */
public class JettyConfig {

    private HttpConfig http;
    private HttpsConfig https;

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    public HttpsConfig getHttps() {
        return https;
    }

    public void setHttps(HttpsConfig https) {
        this.https = https;
    }
}
