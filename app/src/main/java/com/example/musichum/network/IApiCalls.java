package com.example.musichum.network;

import com.example.musichum.models.CartItem;
import com.example.musichum.models.LoginHistory;
import com.example.musichum.models.LoginToken;
import com.example.musichum.models.OrderHistory;
import com.example.musichum.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IApiCalls {

    // USER MICROSERVICE

    @POST("/users/add")
    Call<Void> addUser(User user);

    @POST("/users/login")
    Call<LoginToken> loginUser(String userName, String password);

    @GET("/users/{userName}")
    Call<User> getUserDetails(@Path("userName") String userName, @Body String usertoken);

    @PUT("/users/{userName}/update")
    Call<Void> updateUser(@Path("userName") String username, @Body User user);

    @GET("users/{userName}/history/login/{page}")
    Call<List<LoginHistory>> getLoginHistory(@Path("userName") String userName, @Path("page") int page, @Body String usertoken);

    @GET("users/{userName}/history/order/{page}")
    Call<List<OrderHistory>> getOrderHistory(@Path("userName") String userName, @Path("page") int page, @Body String usertoken);


    // CART MICROSERVICE

    @POST("/cart/{userName}/add")
    Call<Void> addToCart(@Path("userName") String userName, @Body String type, @Body String id, @Body String did, @Body String usertoken);

    @GET("/cart/{userName}/get")
    Call<List<CartItem>> getCart(@Path("userName") String userName, @Body String usertoken);

    @DELETE("/cart/{userName}/delete")
    Call<Void> deleteFromCart(@Path("userName") String userName, @Body String type, @Body String id, @Body String did, @Body String usertoken);

    @POST("/cart/{userName}/checkout")
    Call<Void> checkout(@Path("userName") String userName, @Body String usertoken);

}
