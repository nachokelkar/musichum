package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.LoginToken;
import com.example.musichum.models.LoginWrapper;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.TempCartRetrofitBuilder;
import com.facebook.login.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences =  getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        EditText etUserName = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);

        findViewById(R.id.bt_registerInstead).setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.bt_loginButton).setOnClickListener(view -> {
            if(etPassword.getText().toString().trim().length() == 0 || etUserName.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG).show();
            }
            else{
                Retrofit retrofit = RetrofitBuilder.getInstance();
                IApiCalls iApiCalls = retrofit.create(IApiCalls.class);

                LoginWrapper loginWrapper = new LoginWrapper();
                loginWrapper.setPassword(etPassword.getText().toString().trim());
                loginWrapper.setUserName(etUserName.getText().toString().trim());

                Call<LoginToken> response = iApiCalls.loginUser(loginWrapper);
                response.enqueue(new Callback<LoginToken>() {
                    @Override
                    public void onResponse(Call<LoginToken> call, Response<LoginToken> response) {
                        if(response.code() == 200){
                            Retrofit retrofit1 = TempCartRetrofitBuilder.getInstance();
                            IApiCalls iApiCalls1 = retrofit1.create(IApiCalls.class);

                            Call<Void> mergeResponse = iApiCalls1.mergeCart(etUserName.getText().toString().trim(), sharedPreferences.getString("isLoggedIn", ""));
                            mergeResponse.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response1) {
                                    if(response.code()==200){
                                        Log.d("LOGIN - MERGE", "onResponse: Saved username as " + sharedPreferences.getString("isLoggedIn", ""));
                                    }
                                    else{
                                        Log.d("LOGIN - MERGE", "IMPOSTOR" +response.code());
                                    }
                                    editor.putString("isLoggedIn", etUserName.getText().toString().trim());
                                    editor.putString("usertoken", response.body().getUsertoken());
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.d("LOGIN - MERGE", "onFailure: Merge cart failed");
                                }
                            });
                        }
                        else if(response.code() == 401){
                            Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Error in login, try again later.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginToken> call, Throwable t) {

                    }
                });
            }
        });
    }
}
