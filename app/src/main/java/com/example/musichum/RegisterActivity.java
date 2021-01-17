package com.example.musichum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.User;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 9001;

    private static final String CLIENT_ID = "c317e8cc724d454e8c636c9cfaecbb6e";

    private static final String REDIRECT_URI = "http://localhost:8888";

    private static final int REQUEST_CODE = 1337;

    private SpotifyAppRemote mSpotifyAppRemote;


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

//        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//        builder.setScopes(new String[]{"user-read-private"});
//        AuthorizationRequest request = builder.build();
//        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);



        findViewById(R.id.bt_registerButton).setOnClickListener(view -> {
            if(!isEmpty(et_email) && !isEmpty(et_username) && !isEmpty(et_password)){
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
                        if(response.code() == 200){
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Registration error " +response.code() +".\nPlease try again in some time.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Registration error.\nPlease try again in some time.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(this, "Some fields are missing.\nEnsure you have filled username, email and password fields.", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.bt_googleSignIn).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> signInAccountTask= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(signInAccountTask);
        }

//        else if (requestCode == REQUEST_CODE) {
//            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
//            response.
//
//            if (response.getType() == AuthorizationResponse.Type.TOKEN) {
//                ConnectionParams connectionParams =
//                        new ConnectionParams.Builder(CLIENT_ID)
//                                .setRedirectUri(REDIRECT_URI)
//                                .showAuthView(true)
//                                .build();
//
//                SpotifyAppRemote.connect(this, connectionParams,
//                        new Connector.ConnectionListener() {
//
//                            @Override
//                            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                                mSpotifyAppRemote = spotifyAppRemote;
//                                Log.d("MainActivity", "Connected! Yay!");
//
//                                // Now you can start interacting with App Remote
//                                mSpotifyAppRemote.getUserApi().;
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                Log.e("MainActivity", throwable.getMessage(), throwable);
//
//                                // Something went wrong when attempting to connect! Handle errors here
//                            }
//                        });
//            }
//        }
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
            findViewById(R.id.bt_facebookSignIn).setVisibility(View.GONE);
            Toast.makeText(this, "Google authentication complete.\nEnter a username and password for your profile.", Toast.LENGTH_LONG).show();
        }
        else {
            findViewById(R.id.bt_facebookSignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_googleSignIn).setVisibility(View.VISIBLE);
        }
    }

    private boolean isEmpty(EditText et){
        if(et.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
