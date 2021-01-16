package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichum.adapter.CartRecyclerAdapter;
import com.example.musichum.constants.Constants;
import com.example.musichum.models.CartItem;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity implements CartRecyclerAdapter.CartItemInterface, Constants {
    Retrofit retrofit = RetrofitBuilder.getInstance();
    IApiCalls iApiCalls = retrofit.create(IApiCalls.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        List<CartItem> cartItemList = new ArrayList<>();
        populateCart(cartItemList);
        sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        findViewById(R.id.bt_checkout).setOnClickListener(view -> {
            if(sharedPreferences.getString("isLoggedIn", "").startsWith(":")){
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            }
            else {
                Call<Void> response = iApiCalls.checkout(sharedPreferences.getString("isLoggedIn", ""), sharedPreferences.getString("usertoken", ""));
                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        intent.putExtra("statusCode", response.code());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void populateCart(List<CartItem> cartItems){
        sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("isLoggedIn", "");
        String usertoken = sharedPreferences.getString("usertoken", "");

        findViewById(R.id.bt_home).setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        Call<List<CartItem>> responses = iApiCalls.getCart(username, usertoken);

        responses.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if(response.code() == 200 && response.body() != null){
                    for (CartItem cartItem : response.body()) {
                        cartItems.add(cartItem);
                    }
                    findViewById(R.id.bt_checkout).setEnabled(true);
                    RecyclerView rvCart = findViewById(R.id.rv_cart);
                    CartRecyclerAdapter cartRecyclerAdapter = new CartRecyclerAdapter(cartItems, CartActivity.this);
                    rvCart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                    rvCart.setAdapter(cartRecyclerAdapter);
                }
                else{
                    Toast.makeText(CartActivity.this, "Error " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Failed to get cart. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUserClick(CartItem cartItem) {
        Intent intent;
        switch (cartItem.getType()){
            case TYPE_ALBUM:
                intent = new Intent(this, AlbumActivity.class);
                intent.putExtra(AID, cartItem.getId());
                break;

            case TYPE_SONG:
                intent = new Intent(this, SongActivity.class);
                intent.putExtra(PID, cartItem.getId());
                break;

            default:
                intent = new Intent(this, HomeActivity.class);
        }

        intent.putExtra(TITLE, cartItem.getName());
        intent.putExtra(COVER_URL, cartItem.getCoverUrl());
        intent.putExtra(ALBUM_NAME, cartItem.getAlbumName());
        intent.putExtra(ARTIST, cartItem.getArtist());
        intent.putExtra(COST, cartItem.getCost());
        intent.putExtra(DIST_ID, cartItem.getDid());

        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
