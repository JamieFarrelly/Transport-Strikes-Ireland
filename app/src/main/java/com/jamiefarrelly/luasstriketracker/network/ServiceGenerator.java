package com.jamiefarrelly.luasstriketracker.network;

import com.jamiefarrelly.luasstriketracker.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tmeaney on 03/09/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = Constants.BASE_URL;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

//        httpClient.addInterceptor(chain -> {
//            Request original = chain.request();
//
//            Request.Builder requestBuilder = original.newBuilder()
//                    .header("Accept", "application/json")
//                    .method(original.method(), original.body());
//
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        });


        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();


        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        OkHttpClient client = httpClient.build();
        return builder.client(client).build();
    }
}
