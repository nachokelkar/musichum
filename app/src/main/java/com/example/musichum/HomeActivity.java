package com.example.musichum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichum.adapter.SearchRecyclerAdapter;
import com.example.musichum.constants.Constants;
import com.example.musichum.models.Product;
import com.example.musichum.models.SearchItem;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.TempSearchRetrofitBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity implements SearchRecyclerAdapter.SearchItemInterface, Constants {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RecyclerView recyclerView;
    SearchRecyclerAdapter searchRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("isLoggedIn")){
            editor.putString("isLoggedIn", ":" +UUID.randomUUID().toString());
            editor.putString("usertoken", "GUEST");
            editor.commit();
        }

        findViewById(R.id.bt_userAccount).setOnClickListener(view -> {
            if(sharedPreferences.getString("isLoggedIn", "").startsWith(":") || sharedPreferences.getString("isLoggedIn", "").isEmpty()){
                Log.d("HOME", "onCreate: NOT LOGGED IN");
                startActivity(new Intent(this, RegisterActivity.class));
            }
            else {
                Log.d("HOME", "onCreate: LOGGED IN: " +sharedPreferences.getString("isLoggedIn", ""));
                startActivity(new Intent(this, UserActivity.class));
            }
        });

        findViewById(R.id.bt_cart).setOnClickListener(view -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        List<SearchItem> recommendations = new ArrayList<>();
        generateRecommendations(recommendations);

        findViewById(R.id.bt_searchButton).setOnClickListener(view -> {
            Retrofit retrofit = TempSearchRetrofitBuilder.getInstance();
            IApiCalls iApiCalls = retrofit.create(IApiCalls.class);

            EditText etSearch = findViewById(R.id.et_searchQuery);

            Call<List<SearchItem>> responses = iApiCalls.search(etSearch.getText().toString());
            responses.enqueue(new Callback<List<SearchItem>>() {
                @Override
                public void onResponse(Call<List<SearchItem>> call, Response<List<SearchItem>> response) {
                    if(response.code() == 200 && response.body()!=null){
                        List<SearchItem> searchItems = new ArrayList<>();

                        for (SearchItem searchItem : response.body()) {
                            searchItems.add(searchItem);
                        }

                        recyclerView = findViewById(R.id.rv_searchResults);
                        searchRecyclerAdapter = new SearchRecyclerAdapter(searchItems, HomeActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                        recyclerView.setAdapter(searchRecyclerAdapter);
                    }
                    else {
                        Toast.makeText(HomeActivity.this, "No search results.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<SearchItem>> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Error making search call.", Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void generateRecommendations(List<SearchItem> searchItemList){
        Retrofit retrofit = RetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit.create(IApiCalls.class);

        Call<List<Product>> response = iApiCalls.getRecommendations();
        response.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.body()!=null){
                    for (Product product : response.body()) {
                        SearchItem searchItem = new SearchItem();
                        searchItem.setAlbum(product.getAlbum());
                        searchItem.setArtist(product.getArtist());
                        searchItem.setCoverUrl(product.getCoverUrl());
                        searchItem.setGenre(product.getGenre());
                        searchItem.setId(product.getPid());
                        searchItem.setTitle(product.getTitle());
                        searchItem.setCost(product.getLowestCost());
                        searchItemList.add(searchItem);
                    }

                    recyclerView = findViewById(R.id.rv_searchResults);
                    searchRecyclerAdapter = new SearchRecyclerAdapter(searchItemList, HomeActivity.this);

                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    recyclerView.setAdapter(searchRecyclerAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                SearchItem searchItem = new SearchItem();
                searchItem.setTitle("ERROR");
                searchItem.setArtist("Could not fetch recommendations");
                searchItem.setCost(0);

                searchItemList.add(searchItem);
            }
        });
    }

    @Override
    public void onUserClick(SearchItem searchItem) {
        Intent intent;
        switch (searchItem.getType()){
            case TYPE_ALBUM:
                intent = new Intent(this, AlbumActivity.class);
                intent.putExtra(AID, searchItem.getId());
                break;

            case TYPE_SONG:
                intent = new Intent(this, SongActivity.class);
                intent.putExtra(PID, searchItem.getId());
                break;

            default:
                intent = new Intent(this, HomeActivity.class);
        }

        intent.putExtra(TITLE, searchItem.getTitle());
        intent.putExtra(COVER_URL, searchItem.getCoverUrl());
        intent.putExtra(ALBUM_NAME, searchItem.getAlbum());
        intent.putExtra(ARTIST, searchItem.getArtist());
        intent.putExtra(COST, searchItem.getCost());
        intent.putExtra(SONG_URL, searchItem.getSongUrl());
        intent.putExtra(DIST_ID, searchItem.getDid());

        startActivity(intent);
    }
}
