package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.User;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        String email = getIntent().getStringExtra("emailID");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");

        EditText evEmail = findViewById(R.id.et_email);
        EditText evFirstName = findViewById(R.id.et_firstName);
        EditText evLastName = findViewById(R.id.et_lastName);

        evEmail.setText(email);
        evFirstName.setText(firstName);
        evLastName.setText(lastName);

        findViewById(R.id.bt_update).setOnClickListener(view -> {
            User user = new User();
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
            Retrofit retrofit = RetrofitBuilder.getInstance();
            IApiCalls iApiCalls = retrofit.create(IApiCalls.class);

            user.setEmail(evEmail.getText().toString().trim());
            user.setFirstName(evFirstName.getText().toString().trim());
            user.setLastName(evLastName.getText().toString().trim());
            user.setUserName(sharedPreferences.getString("isLoggedIn", ""));

            Call<Void> response = iApiCalls.updateUser(sharedPreferences.getString("isLoggedIn", "").toString(), user);
            response.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 200){
                        Toast.makeText(UpdateActivity.this, "Successfully updated", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UpdateActivity.this, UserActivity.class));
                    }
                    else{
                        Toast.makeText(UpdateActivity.this, "Error " +response.code() +". Please try again later", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(UpdateActivity.this, "Call failed. Try again later.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
