package me.legrange.services.letsencrypt;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

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
