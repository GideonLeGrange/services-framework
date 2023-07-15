package me.legrange.services.monitor;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import me.legrange.service.ComponentException;


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
