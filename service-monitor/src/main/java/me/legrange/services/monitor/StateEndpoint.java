package me.legrange.services.monitor;

import me.legrange.service.ComponentException;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * @author gideon
 */
@Path("")
public class StateEndpoint {

    @GET
    @Path("")
    public List<String> getMonitors() { 
        return MonitorComponent.getInstance().getMonitorNames();
    }
    
    @GET
    @Path("/{name}")
    public Object getState(@PathParam("name") String name, @QueryParam("flatten") boolean flatten) throws ComponentException {
         return MonitorComponent.getInstance().getMonitorState(name, flatten);
    }
    
}
