package me.legrange.services.panacea;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * @author matthewl
 */
public interface PanaceaService {

    @GET("/json?action=message_send")
    Call<PanaceaApiResponse> message_send(
            @Query("username") final String username,
            @Query("password") final String password,
            @Query("text") final String text,
            @Query("to") final String to);

    @GET("/json?action=messages_get")
    Call<PanaceaShortCodeResponse> messages_get(
            @Query("username") final String username,
            @Query("password") final String password,
            @Query("last_id") final int last_id);
}
