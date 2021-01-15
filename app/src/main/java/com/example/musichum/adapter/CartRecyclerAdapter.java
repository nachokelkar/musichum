package com.example.musichum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichum.R;
import com.example.musichum.models.CartItem;

import java.util.List;

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

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvName = view.findViewById(R.id.tv_title);
            ivCover = view.findViewById(R.id.iv_coverArt);
            tvArtist = view.findViewById(R.id.tv_artistName);
            tvAlbum = view.findViewById(R.id.tv_albumName);
            tvType = view.findViewById(R.id.tv_type);
            tvCost = view.findViewById(R.id.tv_cost);
        }
    }

}
