package me.legrange.service.monitor;

import com.google.gson.Gson;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import me.legrange.service.ComponentException;

/**
 *
 * @author gideon
 */
@Path("")
public class StateEndpoint {

    @GET
    @Path("/monitors")
    public String getMonitors() { 
        return serialize(MonitorComponent.getInstance().getMonitorNames());
    }
    
    @GET
    @Path("/monitors/{name}")
    public String getState(@PathParam("name") String name) throws ComponentException {
        return serialize(MonitorComponent.getInstance().getMonitorState(name));
    }
    
    private String serialize(Object data) {
        return new Gson().toJson(data);
    }
    
}
