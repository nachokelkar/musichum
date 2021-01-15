package com.example.musichum;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(sharedPreferences.getString("isLoggedIn", "") != null){
            finish();
        }
        EditText etUserName = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);

        findViewById(R.id.bt_loginButton).setOnClickListener(view -> {
            if(etPassword.getText().toString().trim().length() == 0 || etUserName.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG);
            }
            else{
                Retrofit retrofit = RetrofitBuilder.getInstance();
                IApiCalls iApiCalls = retrofit.create(IApiCalls.class);
                Call<Void> response = iApiCalls.loginUser(etUserName.getText().toString().trim(), etPassword.getText().toString().trim());
                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            editor.putString("isLoggedIn", etUserName.getText().toString().trim());
                        }
                        else if(response.code() == 401){
                            Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });
    }
}
