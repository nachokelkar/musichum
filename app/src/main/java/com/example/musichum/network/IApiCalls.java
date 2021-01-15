package com.example.musichum.network;

import com.example.musichum.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IApiCalls {
    @POST("/users/add")
    Call<Void> addUser(User user);

    @GET("/{userName}")
    Call<User> getUserDetails(@Path("userName") String userName);
}
