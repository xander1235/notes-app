package com.example.notes.network;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    private static final String BASE_URL = "http://dba953e23e02.ngrok.io";
    private static final String TOKEN = "BDF75C1B-DC42-440F-A006-9CE38A5362A4";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("X-Request-ID", UUID.randomUUID().toString())
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "*/*")
                        .addHeader("token", TOKEN)
                        .build();
                return chain.proceed(request);
            });

            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
