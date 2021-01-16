package com.example.musichum;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musichum.constants.Constants;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;

import retrofit2.Retrofit;

public class AlbumActivity extends AppCompatActivity implements Constants {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Retrofit retrofit = RetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit.create(IApiCalls.class);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        String aid = getIntent().getStringExtra(AID);
        String title = getIntent().getStringExtra(TITLE);
        String coverUrl = getIntent().getStringExtra(COVER_URL);
        int cost = getIntent().getIntExtra(COST, -1);
        String artist = getIntent().getStringExtra(ARTIST);
        String album = getIntent().getStringExtra(ALBUM_NAME);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvArtist = findViewById(R.id.tv_artistName);
        TextView tvAlbum = findViewById(R.id.tv_albumName);
        TextView year = findViewById(R.id.tv_year);

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.close)
                .into((ImageView) findViewById(R.id.iv_coverArt));
    }
}
