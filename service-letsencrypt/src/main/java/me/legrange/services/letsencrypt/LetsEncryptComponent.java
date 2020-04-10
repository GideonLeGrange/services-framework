package me.legrange.services.letsencrypt;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.WithJetty;

public final class LetsEncryptComponent  extends Component<Service, LetsEncryptConfig> implements WithJetty {

    private static LetsEncryptComponent instance;

    public LetsEncryptComponent(Service service) {
        super(service);
    }

    @Override
    public void start(LetsEncryptConfig config) throws ComponentException {
        jetty().addEndpoint("/.well-known/acme-challenge/", ChallengeEndpoint.class);
    }

    @Override
    public String getName() {
        return "lets-encrypt";
    }

    String getChallengeResponse(String token) {
        return "FIXME";
    }

    static LetsEncryptComponent getInstance() {
        return instance;
    }

}
