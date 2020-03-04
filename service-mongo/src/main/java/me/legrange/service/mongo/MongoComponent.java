package me.legrange.service.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

import java.util.Arrays;

/**
 *
 * @author matt-vm
 */
public class MongoComponent extends Component<Service, MongoConfig> {

    private MongoClient mongoClient;

    public MongoComponent(Service service) {
        super(service);
    }

    @Override
    public void start(MongoConfig config) throws ComponentException {
        MongoCredential userCredentials = MongoCredential.createCredential(config.getUsername(), config.getUsersDatabase(), config.getPassword().toCharArray());
        mongoClient = new MongoClient(new ServerAddress(config.getHost(), config.getPort()), Arrays.asList(userCredentials));
    }

    public MongoClient getClient() {
        return mongoClient;
    }

    @Override
    public String getName() {
        return "mongodb";
    }

}
