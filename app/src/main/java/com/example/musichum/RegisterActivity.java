package com.example.musichum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.SpotifyUser;
import com.example.musichum.models.User;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.network.ISpotifyAPI;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.SpotifyRetrofit;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity  {

    private static final int RC_GOOGLE = 9001;

    private static final String CLIENT_ID = "c317e8cc724d454e8c636c9cfaecbb6e";

    private static final String REDIRECT_URI = "com-example-musichum://callback";

    private static final int RC_SPOTIFY = 1337;


    EditText et_username;
    EditText et_email;
    EditText et_firstName;
    EditText et_lastName;
    EditText et_password;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.bt_loginInstead).setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private user-read-email"});
        AuthenticationRequest request = builder.build();
        findViewById(R.id.bt_spotifySignIn).setOnClickListener(view -> {
            AuthenticationClient.openLoginActivity(this, RC_SPOTIFY, request);
        });


        findViewById(R.id.bt_registerButton).setOnClickListener(view -> {
            if(!isEmpty(et_email) && !isEmpty(et_username) && !isEmpty(et_password)){
                User user = new User();
                user.setEmail(et_email.getText().toString().trim());
                user.setUserName(et_username.getText().toString().trim());
                user.setFirstName(et_firstName.getText().toString().trim());
                user.setLastName(et_lastName.getText().toString().trim());
                user.setPassword(et_password.getText().toString().trim());

//                retrofit = RetrofitBuilder.getInstance();

                Call<Void> response = iApiCalls.addUser(user);

                Log.d("RegisterUser", "onCreate: Call ready");

                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            Toast.makeText(view.getContext(), "Registration successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else{
                            Toast.makeText(view.getContext(), "Registration error " +response.code() +".\nPlease try again in some time.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(view.getContext(), "Registration error.\nPlease try again in some time.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(this, "Some fields are missing.\nEnsure you have filled username, email and password fields.", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.bt_googleSignIn).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GOOGLE){
            Task<GoogleSignInAccount> signInAccountTask= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(signInAccountTask);
        }

        else if (requestCode == RC_SPOTIFY) {
            AuthenticationResponse authorizationResponse = AuthenticationClient.getResponse(resultCode, data);

            Log.d("Spotify", "onActivityResult: " +authorizationResponse.getError());
            Log.d("Spotify", "onActivityResult: Auth response " +authorizationResponse.getCode());
            Log.d("Spotify auth token", "Auth token " +authorizationResponse.getAccessToken());

            if (authorizationResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                Log.d("Spotfy", "onActivityResult: AUTH COMPLETE");
                Retrofit spotifyRetrofit = SpotifyRetrofit.getInstance();
                ISpotifyAPI spotifyAPI = spotifyRetrofit.create(ISpotifyAPI.class);

                Call<SpotifyUser> spotifyUserCall = spotifyAPI.getUserDetailsSpotify(authorizationResponse.getAccessToken());

                spotifyUserCall.enqueue(new Callback<SpotifyUser>() {
                    @Override
                    public void onResponse(Call<SpotifyUser> call, Response<SpotifyUser> response) {
                        Log.d("Spotify", "User Call Made: " +response.body().toString());

                        if(response.body()!=null && response.code()==200){
                            et_email.findViewById(R.id.et_email);
                            et_firstName.findViewById(R.id.et_firstName);
                            et_lastName.findViewById(R.id.et_lastName);

                            et_email.setText(response.body().getEmail());
                            et_firstName.setText(response.body().getDisplay_name().split(" ")[0]);
                            et_lastName.setText(response.body().getDisplay_name().split(" ")[1]);
                        }
                    }

                    @Override
                    public void onFailure(Call<SpotifyUser> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Failed.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account){
        if(account!=null){
            et_email = findViewById(R.id.et_email);
            et_firstName = findViewById(R.id.et_firstName);
            et_lastName = findViewById(R.id.et_lastName);

            et_email.setText(account.getEmail());
            et_firstName.setText(account.getGivenName());
            et_lastName.setText(account.getFamilyName());

            findViewById(R.id.bt_googleSignIn).setVisibility(View.GONE);
            findViewById(R.id.bt_spotifySignIn).setVisibility(View.GONE);
            Toast.makeText(this, "Google authentication complete.\nEnter a username and password for your profile.", Toast.LENGTH_LONG).show();
        }
        else {
            findViewById(R.id.bt_spotifySignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_googleSignIn).setVisibility(View.VISIBLE);
        }
    }

    private boolean isEmpty(EditText et){
        if(et.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
