package me.legrange.services.jetty;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import static me.legrange.log.Log.error;

/**
 * A filter that catches ServletExceptions (thrown when user code has
 * generated an error) and logs them. 
 * 
 * @author gideon
 */
public class ErrorFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        try { 
            fc.doFilter(sr, sr1);
        }
        catch ( ServletException ex) {
            if (ex.getCause() != null) {
                error(ex.getCause(), "Unhandled exception in REST endpoint: %s", ex.getCause());
            }
            else {
                error(ex, "Unhandled exception in REST endpoint: %s", ex.getMessage());
            }
            throw ex;
        }
    }

    @Override
    public void destroy() {
    }
    
}
