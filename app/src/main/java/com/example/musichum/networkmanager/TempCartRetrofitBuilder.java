package com.example.musichum.networkmanager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TempCartRetrofitBuilder {
    private static Retrofit instance;

    private TempCartRetrofitBuilder() {
        // private constructor
    }

    public static Retrofit getInstance() {
        if (instance == null) {
            synchronized (com.example.musichum.networkmanager.RetrofitBuilder.class) {
                if (instance == null) {
                    instance = new Retrofit.Builder().baseUrl("http://10.177.1.134:8000")
                            .addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
                }
            }
        }
        return instance;
    }

}
