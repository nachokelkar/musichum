package com.example.musichum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView textView = findViewById(R.id.tv_message);

        int responseCode = getIntent().getIntExtra("statusCode", -1);

        switch (responseCode){
            case 200:
                textView.setText("success");
                findViewById(R.id.bt_end).setOnClickListener(view -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                break;
            default:
                textView.setText("ERROR " +responseCode);
                Toast.makeText(this, "Please press the back button", Toast.LENGTH_LONG).show();
                Button bt_end = findViewById(R.id.bt_end);
                bt_end.setText("Back");
                bt_end.setOnClickListener(view -> {
                    startActivity(new Intent(this, CartActivity.class));
                    finish();
                });
                break;
        }
    }
}
