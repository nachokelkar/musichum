package com.example.musichum;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo : change display message based on error code

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        findViewById(R.id.bt_end).setOnClickListener(view -> {startActivity(new Intent(this, MainActivity.class));});
    }
}
