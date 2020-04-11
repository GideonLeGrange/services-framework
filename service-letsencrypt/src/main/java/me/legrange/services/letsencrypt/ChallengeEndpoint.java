package me.legrange.services.letsencrypt;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Optional;

@Path("")
public class ChallengeEndpoint {

    @GET
    @Path("{token}")
    public String getChallengeResponse(@PathParam("token") final String token) throws LetsEcryptException {
        Optional<String> response = LetsEncryptComponent.getInstance().getChallengeResponse(token);
        if (response.isPresent()) {
            return response.get();
        }
        throw new LetsEcryptException("Cannot find response for challenge");
    }
}
