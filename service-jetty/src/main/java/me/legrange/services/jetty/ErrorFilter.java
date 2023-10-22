package me.legrange.services.jetty;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import static me.legrange.log.Log.error;

/**
 * A filter that catches ServletExceptions (thrown when user code has
 * generated an error) and logs them.
 *
 * @author gideon
 */
public final class ErrorFilter implements Filter {

    @Override
    public void init(FilterConfig fc) {
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        try {
            fc.doFilter(sr, sr1);
        } catch (ServletException ex) {
            if (ex.getCause() != null) {
                error(ex.getCause(), "Unhandled exception in REST endpoint: %s", ex.getCause());
            } else {
                error(ex, "Unhandled exception in REST endpoint: %s", ex.getMessage());
            }
            throw ex;
        }
        catch (IOException ex) {
            error(ex, "IO error (%s)", ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex) {
            error(ex, "Unexpected error (%s)", ex.getMessage(), ex);
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    @Override
    public void destroy() {
    }

}
