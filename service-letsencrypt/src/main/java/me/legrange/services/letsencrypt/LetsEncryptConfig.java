package me.legrange.services.letsencrypt;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LetsEncryptConfig {

    @NotBlank(message = "The domain for which to manage certificates")
    private String domain;
    @NotBlank(message = "The Let's Encrypt URL must be specified")
    private String letsEncryptUrl = "https://acme-staging-v02.api.letsencrypt.org/directory";
//    private String letsEncryptUrl = "https://acme-v02.api.letsencrypt.org/directory";
    @Min(value=1, message="The number of days before expirationt that a certificate will be renewed must be between 1 and 14" )
    @Max(value=14, message="The number of days before expirationt that a certificate will be renewed must be between 1 and 14" )
    private int renewalDays = 7;

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

    public int getRenewalDays() {
        return renewalDays;
    }

    public void setRenewalDays(int renewalDays) {
        this.renewalDays = renewalDays;
    }
}
