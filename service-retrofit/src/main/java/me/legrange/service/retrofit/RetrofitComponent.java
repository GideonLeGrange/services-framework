package me.legrange.service.retrofit;

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

        retrofit = new Retrofit.Builder()
                .baseUrl(config.getBaseUrl())
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <C> C createClient(Class<C> clientCalls) {
        return retrofit.create(clientCalls);
    }

    @Override
    public String getName() {
        return "retrofit";
    }

}
