package com.example.musichum.network;

import com.example.musichum.models.SpotifyUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ISpotifyAPI {

    @GET("/v1/me")
    Call<SpotifyUser> getUserDetailsSpotify(@Header("Authorization") String token);
}
