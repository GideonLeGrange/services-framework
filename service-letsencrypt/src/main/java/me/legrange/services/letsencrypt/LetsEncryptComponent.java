package me.legrange.services.letsencrypt;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.WithJetty;

import java.io.File;

import static java.lang.String.format;

public final class LetsEncryptComponent  extends Component<Service, LetsEncryptConfig> implements WithJetty {

    private static LetsEncryptComponent instance;
    private LetsEncryptConfig config;

    public LetsEncryptComponent(Service service) {
        super(service);
    }

    @Override
    public void start(LetsEncryptConfig config) throws ComponentException {
        this.config = config;
        jetty().addEndpoint("/.well-known/acme-challenge/", ChallengeEndpoint.class);
        if (hasCertificate()) {
            service().submit(this::scheduleRenewalCheck);
        }
        else {
            service().submit(this::obtainCertificate);
        }
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

    private void obtainCertificate() {

    }

    private void scheduleRenewalCheck() {

    }

    /** Determine if a certificate exists for the domain being served. */
    private boolean hasCertificate() {
        return new File(getCertificateFileName()).exists();
    }

    /** Make the certificate file name */
    private String getCertificateFileName() {
        return format("%s/%s.crt", config.getDataDirectory(), config.getDomain());
    }

}
