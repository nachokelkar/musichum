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
import com.example.musichum.models.LoginHistory;

import java.util.List;

public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {
    private final List<LoginHistory> loginHistories;
    LoginItemInterface loginItemInterface;

    public LoginHistoryAdapter(List<LoginHistory> loginHistories, LoginItemInterface itemInterface){
        this.loginHistories = loginHistories;
        this.loginItemInterface = itemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.login_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginHistory loginHistoryItem = loginHistories.get(position);
        holder.tvLoginID.setText(loginHistoryItem.getId());
        holder.tvTimestamp.setText((CharSequence) loginHistoryItem.getTimestamp());
    }

    public interface LoginItemInterface{
        void onUserClick(LoginHistory loginHistoryItem);
    }

    @Override
    public int getItemCount() {
        return loginHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rootView;
        private final TextView tvLoginID;
        private final TextView tvTimestamp;

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvLoginID = view.findViewById(R.id.tv_loginId);
            tvTimestamp = view.findViewById(R.id.tv_timestamp);
        }
    }

}
