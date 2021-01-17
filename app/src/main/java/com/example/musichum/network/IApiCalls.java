package com.example.musichum.network;

import com.example.musichum.models.CartItem;
import com.example.musichum.models.InventoryItem;
import com.example.musichum.models.InventoryResponse;
import com.example.musichum.models.LoginHistory;
import com.example.musichum.models.LoginToken;
import com.example.musichum.models.LoginWrapper;
import com.example.musichum.models.OrderHistory;
import com.example.musichum.models.Product;
import com.example.musichum.models.SearchItem;
import com.example.musichum.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IApiCalls {

    // USER MICROSERVICE

    @POST("/users/add")
    @Headers("Content-Type: application/json")
    Call<Void> addUser(@Body User user);

    @POST("/users/login")
    @Headers("Content-Type: application/json")
    Call<LoginToken> loginUser(@Body LoginWrapper loginWrapper);

    @GET("/users/{userName}")
    Call<User> getUserDetails(@Path("userName") String userName, @Query("usertoken") String usertoken);

    @PUT("/users/{userName}/update")
    Call<Void> updateUser(@Path("userName") String username, @Body User user);

    @GET("users/{userName}/history/login")
    Call<List<LoginHistory>> getLoginHistory(@Path("userName") String userName, @Query("usertoken") String usertoken);

    @GET("users/{userName}/history/order")
    Call<List<OrderHistory>> getOrderHistory(@Path("userName") String userName, @Query("usertoken") String usertoken);


    // CART MICROSERVICE

    @POST("/cart/{userName}/add")
    Call<Void> addToCart(@Path("userName") String userName, @Body String type, @Body String id, @Body String did, @Body String usertoken);

    @GET("/cart/{userName}/get")
    Call<List<CartItem>> getCart(@Path("userName") String userName, @Query("usertoken") String usertoken);

    @DELETE("/cart/{userName}/delete")
    Call<Void> deleteFromCart(@Path("userName") String userName, @Body String type, @Body String id, @Body String did, @Body String usertoken);

    @POST("/cart/{userName}/checkout")
    Call<Void> checkout(@Path("userName") String userName, @Body String usertoken);

    @GET("/cart/{type}/inventory/{id}")
    Call<List<InventoryItem>> getInventory(@Path("id") String id, @Path("type") String type);


    // PRODUCT MICROSERVICE

    @GET("/{pid}")
    Call<Product> getProduct(@Path("pid") String pid);


    // RECOMMENDATIONS MICROSERVICE

    @GET("/recommendations")
    Call<List<Product>> getRecommendations();

    // SEARCH MICROSERVICE

    @POST("/search/{query}")
    Call<List<SearchItem>> search(@Path("query") String searchQuery);

}
