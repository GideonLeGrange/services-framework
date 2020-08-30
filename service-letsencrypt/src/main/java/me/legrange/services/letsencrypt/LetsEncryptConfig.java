package me.legrange.services.letsencrypt;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LetsEncryptConfig {

    @NotBlank(message = "The domain for which to manage certificates")
    private String domain;
    @NotBlank(message = "The Let's Encrypt URL must be specified")
//    private String letsEncryptUrl = "https://acme-staging-v02.api.letsencrypt.org/directory";
    private String letsEncryptUrl = "https://acme-v02.api.letsencrypt.org/directory";

    @NotNull(message = "The certificate issuer must be specified")
    private IssuerConfig issuer;

    public IssuerConfig getIssuer() {
        return issuer;
    }

    public void setIssuer(IssuerConfig issuer) {
        this.issuer = issuer;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLetsEncryptUrl() {
        return letsEncryptUrl;
    }

    public void setLetsEncryptUrl(String letsEncryptUrl) {
        this.letsEncryptUrl = letsEncryptUrl;
    }


}
