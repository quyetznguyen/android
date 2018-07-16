package space.ntq.tutopassport.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import space.ntq.tutopassport.BuildConfig;

public class RetrofitBuilder {

    private static final String BASE_URL= "http://10.0.3.2/tuto_passport/public/api/";

    private final static OkHttpClient client = buildClient();
    private static Retrofit retrofit;

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
}
