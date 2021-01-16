package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(!sharedPreferences.contains("isLoggedIn")){
            editor.putString("isLoggedIn", ":" +UUID.randomUUID().toString());
            editor.putString("usertoken", "GUEST");
        }

        findViewById(R.id.bt_userAccount).setOnClickListener(view -> {
            if(sharedPreferences.getString("isLoggedIn", "").startsWith(":")){
                startActivity(new Intent(this, UserActivity.class));
            }
            else {
                startActivity(new Intent(this, RegisterActivity.class));
            }
        });

        findViewById(R.id.bt_cart).setOnClickListener(view -> {
            startActivity(new Intent(this, CartActivity.class));
        });


        // todo : implement search recycle view
    }
}
