package me.legrange.service.retrofit;

import com.google.gson.Gson;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.retrofit.auth.AuthInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author matt-vm
 */
public class RetrofitComponent extends Component<Service, RetrofitConfig> {

    private Retrofit retrofit;

    private RetrofitConfig config;

    private OkHttpClient client;

    private Gson gson;

    public RetrofitComponent(Service service) {
        super(service);
    }

    @Override
    public void start(RetrofitConfig config) throws ComponentException {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        switch (config.getAuthMode()) {
            case AUTH_TOKEN:
                clientBuilder.addInterceptor(new AuthInterceptor(config.getAutorizationToken()));
                break;
            case BASIC_AUTH:
                clientBuilder.addInterceptor(new AuthInterceptor(config.getBasicAuthPassword(), config.getBasicAuthPassword()));
                break;
            case NONE:
                break;
            default:
                throw new ComponentException(String.format("AuthMode '%s' not implemented - BUG!", config.getAuthMode().name()));
        }

        if (config.isEnableHttpLogging()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);
        }
        this.config = config;
        this.client = clientBuilder.build();
        this.gson = new Gson();
    }

    public <C> C createClient(Class<C> clientCalls) {
        retrofit = new Retrofit.Builder()
                .baseUrl(config.getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(clientCalls);
    }

    public <C> C createClient(Class<C> clientCalls, Gson gson) {
        this.gson = gson;
        return createClient(clientCalls);
    }

    @Override
    public String getName() {
        return "retrofit";
    }

}
