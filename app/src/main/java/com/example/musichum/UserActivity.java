package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichum.adapter.LoginHistoryAdapter;
import com.example.musichum.adapter.OrderHistoryAdapter;
import com.example.musichum.models.LoginHistory;
import com.example.musichum.models.OrderHistory;
import com.example.musichum.models.User;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserActivity extends AppCompatActivity implements LoginHistoryAdapter.LoginItemInterface, OrderHistoryAdapter.OrderItemInterface {
    SharedPreferences sharedPreferences;
    Retrofit retrofit;
    IApiCalls iApiCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        retrofit = RetrofitBuilder.getInstance();
        iApiCalls = retrofit.create(IApiCalls.class);

        List<LoginHistory> loginHistoryList = new ArrayList<>();
        populateLoginHistory(loginHistoryList);

        List<OrderHistory> orderHistoryList = new ArrayList<>();

        findViewById(R.id.bt_logout).setOnClickListener(view -> {
            editor.clear();
            editor.commit();


            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });


        if(sharedPreferences.getString("isLoggedIn", "").startsWith(":")){
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        else{
            TextView tvUsername = findViewById(R.id.tv_username);
            TextView tvEmail = findViewById(R.id.tv_email);
            TextView tvName = findViewById(R.id.tv_name);

            Log.d("USERDETAILS", "onCreate: logged in username " +sharedPreferences.getString("isLoggedIn", ""));
            Call<User> response = iApiCalls.getUserDetails(sharedPreferences.getString("isLoggedIn", ""), sharedPreferences.getString("usertoken", ""));
            response.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.code() == 200){
                        Log.d("USERDETAILS", "onResponse: " +response.body().getUserName());
                        User user = response.body();
                        tvUsername.setText(user.getUserName());
                        tvEmail.setText(user.getEmail());
                        tvName.setText(user.getFirstName() + " " + user.getLastName());

                        findViewById(R.id.bt_update).setOnClickListener(view -> {
                            Intent intent = new Intent(UserActivity.this, UpdateActivity.class);
                            intent.putExtra("emailID", user.getEmail());
                            intent.putExtra("firstName", user.getFirstName());
                            intent.putExtra("lastName", user.getLastName());
                            startActivity(intent);
                        });
                    }
                    else {
                        Toast.makeText(UserActivity.this, "Error " +response.code() +" while fetching details.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(UserActivity.this, "Could not fetch data. Please try again later.", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    private void populateLoginHistory(List<LoginHistory> loginHistories){
        String username = sharedPreferences.getString("isLoggedIn", "");
        String usertoken = sharedPreferences.getString("usertoken", "");

        Call<List<LoginHistory>> responses = iApiCalls.getLoginHistory(username, usertoken);

        responses.enqueue(new Callback<List<LoginHistory>>() {
            @Override
            public void onResponse(Call<List<LoginHistory>> call, Response<List<LoginHistory>> response) {
                if(response.body() != null){
                    for(LoginHistory loginHistory : response.body()){
                        loginHistories.add(loginHistory);
                    }
                    findViewById(R.id.bt_checkout).setEnabled(true);
                    RecyclerView rvCart = findViewById(R.id.rv_loginHistory);
                    LoginHistoryAdapter loginHistoryAdapter = new LoginHistoryAdapter(loginHistories, UserActivity.this);
                    rvCart.setLayoutManager(new LinearLayoutManager(UserActivity.this));
                    rvCart.setAdapter(loginHistoryAdapter);
                }
                else{
                    Toast.makeText(UserActivity.this, "Couldn't fetch login data. Error " +response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<LoginHistory>> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Failed to get login history. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateOrderHistory(List<OrderHistory> orderHistories){
        String username = sharedPreferences.getString("isLoggedIn", "");
        String usertoken = sharedPreferences.getString("usertoken", "");

        Call<List<OrderHistory>> responses = iApiCalls.getOrderHistory(username, usertoken);

        responses.enqueue(new Callback<List<OrderHistory>>() {
            @Override
            public void onResponse(Call<List<OrderHistory>> call, Response<List<OrderHistory>> response) {
                if(response == null){
                    Toast.makeText(UserActivity.this, "Couldn't fetch order data", Toast.LENGTH_LONG).show();
                }
                else{
                    for(OrderHistory orderHistory : response.body()){
                        orderHistories.add(orderHistory);
                    }
                    findViewById(R.id.bt_checkout).setEnabled(true);
                    RecyclerView rvCart = findViewById(R.id.rv_orderHistory);
                    OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(orderHistories, UserActivity.this);
                    rvCart.setLayoutManager(new LinearLayoutManager(UserActivity.this));
                    rvCart.setAdapter(orderHistoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<OrderHistory>> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Failed to get login history. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUserClick(LoginHistory loginHistoryItem) {

    }

    @Override
    public void onUserClick(OrderHistory orderHistory) {

    }
}
