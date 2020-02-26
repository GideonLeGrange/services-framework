package me.legrange.services.panacea;

import javax.validation.constraints.NotBlank;
import me.legrange.config.Configuration;

/**
 *
 * @author matthewl
 */
public class PanaceaAPIConfig extends Configuration {

    @NotBlank(message = "The Panacea API URL is required")
    private String apiURL;

    @NotBlank(message = "The Panacea API username is required")
    private String username;

    @NotBlank(message = "The Panacea API password is required")
    private String password;

    @NotBlank(message = "Please provide a Date Time format to expect from the api")
    private String dateTimeFormat;

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

}
