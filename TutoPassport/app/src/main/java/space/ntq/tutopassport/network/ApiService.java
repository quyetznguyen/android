package space.ntq.tutopassport.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import space.ntq.tutopassport.entites.AccessToken;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name,
                               @Field("email") String email,
                               @Field("password") String password);


}
