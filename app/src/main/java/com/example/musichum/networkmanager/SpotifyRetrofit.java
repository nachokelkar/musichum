package com.example.musichum.networkmanager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyRetrofit {
    private static Retrofit instance;

    private SpotifyRetrofit() {
        // private constructor
    }

    public static Retrofit getInstance() {
        if (instance == null) {
            synchronized (com.example.musichum.networkmanager.RetrofitBuilder.class) {
                if (instance == null) {
                    instance = new Retrofit.Builder().baseUrl("https://api.spotify.com/")
                            .addConverterFactory(GsonConverterFactory.create()).client(new OkHttpClient()).build();
                }
            }
        }
        return instance;
    }

}
