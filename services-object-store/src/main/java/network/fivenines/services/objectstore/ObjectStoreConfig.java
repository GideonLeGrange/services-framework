package network.fivenines.services.objectstore;

import javax.validation.constraints.NotBlank;

/**
 * Configuration object - Object store setup
 *
 * @author gideon
 */
public class ObjectStoreConfig {

    @NotBlank(message = "The object store data directory must be specified")
    private String dataDirectory;

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

}
