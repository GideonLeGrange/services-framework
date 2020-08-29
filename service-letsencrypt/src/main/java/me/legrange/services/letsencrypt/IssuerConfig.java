package me.legrange.services.letsencrypt;

import javax.validation.constraints.NotBlank;

public class IssuerConfig {

    @NotBlank(message = "Issuer organization must be provided")
    private String organization;

    private String country = "";
    private String locality = "";
    private String organizationalUnit = "";
    private String state = "";



    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
