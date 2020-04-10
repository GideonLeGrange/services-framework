package me.legrange.services.letsencrypt;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("")
public class ChallengeEndpoint {

    @GET
    @Path("{token}")
    public String getChallengeResponse(@PathParam("token") final String token) {
        return LetsEncryptComponent.getInstance().getChallengeResponse(token);
    }
}
