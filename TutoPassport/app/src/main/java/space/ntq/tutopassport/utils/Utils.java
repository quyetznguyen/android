package space.ntq.tutopassport.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import space.ntq.tutopassport.entites.ApiError;
import space.ntq.tutopassport.network.RetrofitBuilder;

public class Utils {
    public static ApiError convertError (ResponseBody responseBody){
        Converter<ResponseBody, ApiError> converter = RetrofitBuilder.getRetrofit()
                .responseBodyConverter(ApiError.class,new Annotation[0]);
        ApiError apiError=null;
        try {
             apiError = converter.convert(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }
}
