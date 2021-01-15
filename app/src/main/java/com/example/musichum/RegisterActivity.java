package com.example.musichum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.User;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Retrofit retrofit = RetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit.create(IApiCalls.class);

        EditText et_username = findViewById(R.id.et_username);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_firstName = findViewById(R.id.et_firstName);
        EditText et_lastName = findViewById(R.id.et_lastName);
        EditText et_password = findViewById(R.id.et_password);

        findViewById(R.id.bt_registerButton).setOnClickListener(view -> {
            if(isEmpty(et_email) && isEmpty(et_username) && isEmpty(et_password)){
                User user = new User();
                user.setEmail(et_email.getText().toString().trim());
                user.setUserName(et_username.getText().toString().trim());
                user.setFirstName(et_firstName.getText().toString().trim());
                user.setLastName(et_lastName.getText().toString().trim());
                user.setPassword(et_password.getText().toString().trim());
                Call<Void> response = iApiCalls.addUser(user);
                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Registration error.\nPlease try again in some time.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean isEmpty(EditText et){
        if(et.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
