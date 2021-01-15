package com.example.musichum;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichum.models.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        List<CartItem> cartItemList;
    }

    private void populateCart(List<CartItem> cartItems){

    }
}
