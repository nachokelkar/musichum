package com.example.musichum.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichum.CartActivity;
import com.example.musichum.R;
import com.example.musichum.models.CartItem;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.TempCartRetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {
    private final List<CartItem> cartItemList;
    CartItemInterface cartItemInterface;

    public CartRecyclerAdapter(List<CartItem> cartItems, CartItemInterface itemInterface){
        this.cartItemList = cartItems;
        this.cartItemInterface = itemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.tvCost.setText(cartItem.getCost());
        holder.tvType.setText(cartItem.getType());
        holder.tvAlbum.setText(cartItem.getAlbumName());
        holder.tvArtist.setText(cartItem.getArtist());
        holder.tvName.setText(cartItem.getName());
        Glide.with(holder.itemView.getContext())
                .load(cartItem.getCoverUrl())
                .placeholder(R.drawable.close)
                .into(holder.ivCover);

        holder.btRemove.setOnClickListener(view -> {
            Retrofit retrofit = TempCartRetrofitBuilder.getInstance();
            IApiCalls iApiCalls = retrofit.create(IApiCalls.class);
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);

            Call<Void> response = iApiCalls.deleteFromCart(sharedPreferences.getString("isLoggedIn", ""), cartItem.getType(), cartItem.getId(), cartItem.getDid(), sharedPreferences.getString("usertoken", ""));

            response.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    view.getContext().startActivity(new Intent(view.getContext(), CartActivity.class));
                    ((Activity)(view.getContext())).finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(view.getContext(), "Error removing item. Please try again later.", Toast.LENGTH_LONG);
                }
            });
        });
    }

    public interface CartItemInterface{
        void onUserClick(CartItem cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rootView;
        private final ImageView ivCover;
        private final TextView tvName;
        private final TextView tvArtist;
        private final TextView tvAlbum;
        private final TextView tvType;
        private final TextView tvCost;
        private final Button btRemove;

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvName = view.findViewById(R.id.tv_title);
            ivCover = view.findViewById(R.id.iv_coverArt);
            tvArtist = view.findViewById(R.id.tv_artistName);
            tvAlbum = view.findViewById(R.id.tv_albumName);
            tvType = view.findViewById(R.id.tv_type);
            tvCost = view.findViewById(R.id.tv_cost);
            btRemove = view.findViewById(R.id.bt_removeItem);
        }
    }

}
