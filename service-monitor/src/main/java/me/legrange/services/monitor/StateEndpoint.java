package me.legrange.services.monitor;

import me.legrange.service.ComponentException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

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

    @GET
    @Path("/{name}/{variable}")
    public Object getSpecificState(@PathParam("name") String name, @PathParam("variable") String variable) throws ComponentException {
        return MonitorComponent.getInstance().getMonitorState(name, variable);
    }

}
