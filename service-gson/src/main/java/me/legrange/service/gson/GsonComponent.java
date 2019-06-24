package me.legrange.service.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

/**
 *
 * @author gideon
 */
public class GsonComponent extends Component<Service, GsonConfig> implements WithLogging {

    private final JsonParser parser;
    private final Gson gson;

    public GsonComponent(Service service) {
        super(service);
        parser = new JsonParser();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void start(GsonConfig conf) throws ComponentException {
    }

    @Override
    public String getName() {
        return "gson";
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {

        }
    }

}
