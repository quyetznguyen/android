package space.ntq.tutopassport.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import space.ntq.tutopassport.BuildConfig;
import space.ntq.tutopassport.TokenManager;
import space.ntq.tutopassport.entites.ApiError;
import space.ntq.tutopassport.utils.Utils;

public class RetrofitBuilder {

    private static final String BASE_URL= "http://192.168.1.3/tuto_passport/public/api/";

    private final static OkHttpClient client = buildClient();
    private static Retrofit retrofit = buiRetrofit(client);

    private static OkHttpClient buildClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder1 = request.newBuilder()
                                .addHeader("Accept","application/json")
                                .addHeader("Connection","close");

                        request = builder1.build();

                        return chain.proceed(request);
                    }
                });

        if (BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return
                 builder.build();
    }

    private static Retrofit buiRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager){

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if (tokenManager.getToken().getAccessToken() != null){
                    builder.addHeader("Authorization","Bearer "+tokenManager.getToken().getAccessToken());

                }
                request = builder.build();
                return chain.proceed(request);

            }
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return  newRetrofit.create(service);
    }

    public static Retrofit getRetrofit(){
        return retrofit;
    }


}
