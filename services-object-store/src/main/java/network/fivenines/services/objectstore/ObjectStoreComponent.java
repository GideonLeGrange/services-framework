package network.fivenines.services.objectstore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

/**
 *
 * @author gideon
 */
public class ObjectStoreComponent<S extends Service> extends Component<S, ObjectStoreConfig> implements WithLogging {

    private String dataDir;
    private Gson gson;

    public ObjectStoreComponent(S service) {
        super(service);
    }

    @Override
    public void start(ObjectStoreConfig config) throws ComponentException {
        gson = new GsonBuilder().setPrettyPrinting().create();
        dataDir = config.getDataDirectory();
    }

    @Override
    public String getName() {
        return "objectStore";
    }

    public <T> T readObject(String key, Class<T> type) throws ObjectStoreException {
        File file = new File(fileName(key));
        if (!file.exists()) {
            throw new ObjectStoreException(format("File '%s' does not exist", file.getAbsolutePath()));
        }
        if (!file.isFile()) {
            throw new ObjectStoreException(format("File '%s' is not a normal file", file.getAbsolutePath()));
        }
        if (!file.canRead()) {
            throw new ObjectStoreException(format("File '%s' does is not readable", file.getAbsolutePath()));
        }
        try {
            T data = gson.fromJson(new FileReader(file), type);
            if (data == null) {
                throw new ObjectStoreException(format("Could not read any data from '%s'", file.getAbsolutePath()));
            }
            return data;
        } catch (FileNotFoundException ex) {
            throw new ObjectStoreException(format("File '%s' was not found", file.getAbsolutePath()));
        } catch (JsonIOException ex) {
            throw new ObjectStoreException(format("JSON IO Error reading file '%s' (%s)", file.getAbsolutePath(), ex.getMessage()));
        } catch (JsonSyntaxException ex) {
            throw new ObjectStoreException(format("JSON error reading file '%s' (%s)", file.getAbsolutePath(), ex.getMessage()));
        }
    }

    public <T> void writeObject(String key, T data) throws ObjectStoreException {
        File file = new File(fileName(key));
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                try {
                    Files.createDirectory(file.getParentFile().toPath());
                } catch (IOException ex) {
                    throw new ObjectStoreException(format("Cannot create directory '%s' (%s)", file.getParent(), ex.getMessage()));
                }
            }
        } else {
            if (!file.canWrite()) {
                throw new ObjectStoreException(format("Cannot write to file directory '%s'", file.getPath()));
            }
        }
        try {
            try (FileWriter out = new FileWriter(file)) {
                out.write(gson.toJson(data));
            }
        } catch (IOException ex) {
            throw new ObjectStoreException(format("IO error writing to file '%s' (%s)", file.getPath(), ex.getMessage()));
        } catch (JsonIOException ex) {
            throw new ObjectStoreException(format("JSON IO error writing to file '%s' (%s)", file.getPath(), ex.getMessage()));
        }
    }

    public boolean hasObject(String key) {
        File file = new File(fileName(key));
        return file.exists() && file.isFile() && file.canRead();
    }

    private String fileName(String key) {
        return format("%s/%s.json", dataDir, key);
    }

}
