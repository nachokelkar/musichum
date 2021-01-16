package com.example.musichum.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichum.AlbumActivity;
import com.example.musichum.HomeActivity;
import com.example.musichum.R;
import com.example.musichum.SongActivity;
import com.example.musichum.constants.Constants;
import com.example.musichum.models.CartItem;
import com.example.musichum.models.SearchItem;

import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> implements Constants {
    private final List<SearchItem> searchItems;
    SearchItemInterface searchItemInterface;

    public SearchRecyclerAdapter(List<SearchItem> searchItems, SearchItemInterface itemInterface){
        this.searchItems = searchItems;
        this.searchItemInterface = itemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchItem searchItem = searchItems.get(position);
        holder.tvCost.setText((int) searchItem.getLowestCost());
        holder.tvType.setText(searchItem.getType());
        holder.tvAlbum.setText(searchItem.getAlbum());
        holder.tvArtist.setText(searchItem.getArtist());
        holder.tvName.setText(searchItem.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(searchItem.getCoverUrl())
                .placeholder(R.drawable.close)
                .into(holder.ivCover);

        holder.btView.setOnClickListener(v -> {
            Intent intent;
            switch (searchItem.getType()){
                case TYPE_ALBUM:
                    intent = new Intent(v.getContext(), AlbumActivity.class);
                    intent.putExtra(AID, searchItem.getId());
                    break;

                case TYPE_SONG:
                    intent = new Intent(v.getContext(), SongActivity.class);
                    intent.putExtra(AID, searchItem.getId());
                    break;

                default:
                    intent = new Intent(v.getContext(), HomeActivity.class);
            }

            intent.putExtra(TITLE, searchItem.getTitle());
            intent.putExtra(COVER_URL, searchItem.getCoverUrl());
            intent.putExtra(ALBUM_NAME, searchItem.getAlbum());
            intent.putExtra(ARTIST, searchItem.getArtist());
            intent.putExtra(COST, searchItem.getLowestCost());

            v.getContext().startActivity(intent);

        });
    }

    public interface SearchItemInterface{
        void onUserClick(SearchItem searchItem);
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rootView;
        private final ImageView ivCover;
        private final TextView tvName;
        private final TextView tvArtist;
        private final TextView tvAlbum;
        private final TextView tvType;
        private final TextView tvCost;
        private final Button btView;

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvName = view.findViewById(R.id.tv_title);
            ivCover = view.findViewById(R.id.iv_coverArt);
            tvArtist = view.findViewById(R.id.tv_artistName);
            tvAlbum = view.findViewById(R.id.tv_albumName);
            tvType = view.findViewById(R.id.tv_type);
            tvCost = view.findViewById(R.id.tv_cost);
            btView = view.findViewById(R.id.bt_viewItem);
        }
    }

}
