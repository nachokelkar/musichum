package com.example.musichum;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichum.adapter.StockRecyclerAdapter;
import com.example.musichum.constants.Constants;
import com.example.musichum.models.InventoryItem;
import com.example.musichum.models.Product;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.TempCartRetrofitBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SongActivity extends AppCompatActivity implements StockRecyclerAdapter.InventoryItemInterface, Constants {
    String title;
    String artist;
    String album;
    String pid;
    float cost;
    String coverUrl;
    String songUrl;
    String did;

    boolean shouldPlay = true;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Retrofit retrofit = RetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit.create(IApiCalls.class);
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        pid = getIntent().getStringExtra(PID);
        title = getIntent().getStringExtra(TITLE);
        coverUrl = getIntent().getStringExtra(COVER_URL);
        cost = getIntent().getFloatExtra(COST, -1);
        artist = getIntent().getStringExtra(ARTIST);
        album = getIntent().getStringExtra(ALBUM_NAME);
        songUrl = getIntent().getStringExtra(SONG_URL);
        did = getIntent().getStringExtra(DIST_ID);


        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvArtist = findViewById(R.id.tv_artistName);
        TextView tvAlbum = findViewById(R.id.tv_albumName);
        TextView year = findViewById(R.id.tv_year);
        ImageView ivCover = findViewById(R.id.iv_coverArt);

        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(songUrl);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mp.getCurrentPosition()<3000){
            shouldPlay = true;
        }
        findViewById(R.id.bt_media).setOnClickListener(view -> {

            if(!isPlaying && shouldPlay){
                mp.start();
                ((Button)findViewById(R.id.bt_media)).setText("Pause");
            }
            else{
                mp.pause();
                ((Button)findViewById(R.id.bt_media)).setText("Play");
            }
            isPlaying = !isPlaying;

            });

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.close)
                .into((ImageView) findViewById(R.id.iv_coverArt));

        Call<Product> response = iApiCalls.getProduct(pid);
        response.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.body() != null && response.code() == 200){
                    pid = response.body().getPid();
                    title = response.body().getTitle();
                    artist = response.body().getArtist();
                    album = response.body().getAlbum();
                    coverUrl = response.body().getCoverUrl();
                    cost = response.body().getLowestCost();
                    float rating = response.body().getAverageRating();
                    String genre = response.body().getGenre();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

        tvTitle.setText(title);
        tvAlbum.setText(album);
        tvArtist.setText(artist);
        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.close)
                .into(ivCover);

        List<InventoryItem> inventoryItemList = new ArrayList<>();
        generateInventory(inventoryItemList, pid, this);

    }

    private void generateInventory(List<InventoryItem> inventoryItems, String pid, SongActivity songActivity){
        Retrofit retrofit2 = TempCartRetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit2.create(IApiCalls.class);

        Call<List<InventoryItem>> responses = iApiCalls.getInventory(pid, TYPE_SONG);
        responses.enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                Log.d("RETROFIT CALL", "onResponse: RECEIVED A RESPONSE");
                if(response.code() == 200 && response.body() != null){
                    Log.d("INVENTORY", "onResponse: Received inventory");

                    for(InventoryItem inventoryItem : response.body()){
                        inventoryItems.add(inventoryItem);
                        Log.d("INVENTITEM", "onResponse: " +inventoryItem.getId());
                    }

                    RecyclerView rvDistributors = findViewById(R.id.rv_stock);
                    StockRecyclerAdapter stockRecyclerAdapter = new StockRecyclerAdapter(inventoryItems, SongActivity.this);
                    rvDistributors.setLayoutManager(new LinearLayoutManager(SongActivity.this));
                    rvDistributors.setAdapter(stockRecyclerAdapter);
                }
                else{
                    InventoryItem inventoryItem = new InventoryItem();
                    inventoryItem.setId(getIntent().getStringExtra(PID));
                    Log.d("SONGACTIVITY - ", "onFailure ERORR: " +inventoryItem.getId());
                    inventoryItem.setType(TYPE_SONG);
                    inventoryItem.setDid(getIntent().getStringExtra(DIST_ID));
                    inventoryItem.setCost(getIntent().getFloatExtra(COST, -1));

                    inventoryItems.add(inventoryItem);
                    RecyclerView rvDistributors = findViewById(R.id.rv_stock);
                    StockRecyclerAdapter stockRecyclerAdapter = new StockRecyclerAdapter(inventoryItems, SongActivity.this);
                    rvDistributors.setLayoutManager(new LinearLayoutManager(SongActivity.this));
                    rvDistributors.setAdapter(stockRecyclerAdapter);

//                    Toast.makeText(SongActivity.this, "Error " +response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {

                Log.d("TAG", "onFailure: " + songActivity.getIntent().getStringExtra(PID));

                InventoryItem inventoryItem = new InventoryItem();
                inventoryItem.setId(SongActivity.this.getIntent().getStringExtra(PID));
                inventoryItem.setType(TYPE_SONG);
                inventoryItem.setDid(getIntent().getStringExtra(DIST_ID));
                inventoryItem.setCost(getIntent().getFloatExtra(COST, -1));
                Log.d("TAG", "onFailure: " +inventoryItem.getId());


                inventoryItems.add(inventoryItem);
                RecyclerView rvDistributors = findViewById(R.id.rv_stock);
                StockRecyclerAdapter stockRecyclerAdapter = new StockRecyclerAdapter(inventoryItems, SongActivity.this);
                rvDistributors.setLayoutManager(new LinearLayoutManager(SongActivity.this));
                rvDistributors.setAdapter(stockRecyclerAdapter);

                Toast.makeText(SongActivity.this, "Error fetching distributors.\nPlease try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(){

    }

    @Override
    public void onUserClick(InventoryItem inventoryItem) {

    }
}
