package me.legrange.services.panacea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import me.legrange.log.Log;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author matthewl
 */
public class PanaceaSMSComponent extends Component<Service, PanaceaAPIConfig> {

    private PanaceaAPIConfig config;

    public PanaceaSMSComponent(Service service) {
        super(service);
    }


    private PanaceaService getPanaceaService() {

        Gson gson = new GsonBuilder().setDateFormat(config.getDateTimeFormat()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getApiURL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(PanaceaService.class);
    }

    public void sendSMS(final String text, final String number) throws PanaceaAPIException {

        try {
            Response<PanaceaApiResponse> response = getPanaceaService().message_send(config.getUsername(), config.getPassword(), text, number).execute();

            if (response.isSuccessful() && response.body().getStatus() == 1) {
                Log.info("SMS Successfully Sent to " + number);
            } else {
                throw new PanaceaAPIException(response.body().getMessage());
            }
        } catch (IOException ex) {
            throw new PanaceaAPIException(ex.getMessage());
        }
    }

    public List<ShortCodeMessage> getMessagesForShortcode(final int lastId) throws PanaceaAPIException {
        try {
            Response<PanaceaShortCodeResponse> response = getPanaceaService().messages_get(config.getUsername(), config.getPassword(), lastId).execute();

            if (response.isSuccessful() && response.body().getStatus() == 0) {
                if (response.body().getDetails() != null) {
                    return response.body().getDetails().stream().sorted(Comparator.comparing(ShortCodeMessage::getId)).collect(Collectors.toList());
                } else {
                    return Collections.EMPTY_LIST;
                }
            } else {
                throw new PanaceaAPIException(response.body().getMessage());
            }
        } catch (IOException ex) {
            throw new PanaceaAPIException(ex.getMessage());
        }
    }

    @Override
    public void start(PanaceaAPIConfig config) throws ComponentException {
        this.config = config;
    }

    @Override
    public String getName() {
        return "panaceaSMS";
    }

}
