package space.ntq.tutopassport.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import space.ntq.tutopassport.entites.AccessToken;
import space.ntq.tutopassport.PostResponse;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name,
                               @Field("email") String email,
                               @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login (@Field("username") String name,
                             @Field("password") String password);
    @POST("logout")
    @FormUrlEncoded
    Call<AccessToken> logout(@Field("access_token")String accessToken);


    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh (@Field("refresh_token") String refreshToken);

    @POST("social_auth")
    @FormUrlEncoded
    Call<AccessToken> socialAuth(@Field("name") String name,
                                 @Field("email") String email,
                                 @Field("provider") String provider,
                                 @Field("provider_user_id") String providerUserId);

    @GET("posts")
    Call<PostResponse> posts();
}
