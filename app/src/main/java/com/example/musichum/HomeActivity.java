package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.bt_userAccount).setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(sharedPreferences.contains("isLoggedIn")){
                startActivity(new Intent(this, UserActivity.class));
            }
            else {
                startActivity(new Intent(this, RegisterActivity.class));
            }
        });

        // todo : implement search, recycle view
    }
}
