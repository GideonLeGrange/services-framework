package me.legrange.service.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import static java.lang.String.format;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

/**
 *
 * @author gideon
 */
public class GsonComponent extends Component<Service, GsonConfig> {

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

    public <T> T deserialize(String json, Class<T> clazz) throws GsonException {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException ex) {
            throw new GsonException(format("Error de-serializing JSON to obect (%s)", ex.getMessage()), ex);
        } catch (RuntimeException ex) {
            throw new GsonException(format("Unexpected error de-serializing JSON to obect (%s)", ex.getMessage()), ex);
        }
    }

    public <T> String serialize(T data) throws GsonException {
        try {
            return gson.toJson(data);
        } catch (RuntimeException ex) {
            throw new GsonException(format("Unexpected error serializing to obect JSON (%s)", ex.getMessage()), ex);
        }
    }

}
