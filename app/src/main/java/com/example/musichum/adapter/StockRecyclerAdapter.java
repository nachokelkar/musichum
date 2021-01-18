package com.example.musichum.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichum.R;
import com.example.musichum.models.CartWrapper;
import com.example.musichum.models.InventoryItem;
import com.example.musichum.network.IApiCalls;
import com.example.musichum.networkmanager.RetrofitBuilder;
import com.example.musichum.networkmanager.TempCartRetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.ViewHolder> {
    private final List<InventoryItem> inventoryItems;
    InventoryItemInterface inventoryItemInterface;
    SharedPreferences sharedPreferences;

    public StockRecyclerAdapter(List<InventoryItem> inventoryItems, InventoryItemInterface itemInterface){
        this.inventoryItems = inventoryItems;
        this.inventoryItemInterface = itemInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem inventoryItem = inventoryItems.get(position);
        holder.tvDistName.setText(inventoryItem.getDid());
        holder.tvCost.setText(inventoryItem.getCost()+"");

        Retrofit retrofit2 = TempCartRetrofitBuilder.getInstance();
        IApiCalls iApiCalls = retrofit2.create(IApiCalls.class);

        holder.btAddToCart.setOnClickListener(v -> {
            Button bt = holder.btAddToCart;

            if(bt.getText().toString().equals("ADD TO CART")){
                Log.d("TAG", "onBindViewHolder: inside if");
                CartWrapper cartWrapper = new CartWrapper();
                cartWrapper.setDid(inventoryItem.getDid());
                cartWrapper.setType(inventoryItem.getType());
                cartWrapper.setPid(inventoryItem.getId());

                SharedPreferences sharedPreferences1 = ((Activity) v.getContext()).getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);

                Call<Void> response = iApiCalls.addToCart(sharedPreferences1.getString("isLoggedIn", ""), cartWrapper);

                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            holder.btAddToCart.setText("REMOVE FROM CART");
                        }
                        else{
                            Toast.makeText(v.getContext(), "Error " +response.code() +". Please try again later.", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(v.getContext(), "FAIL", Toast.LENGTH_LONG).show();
                    }
                });

            }

            else if(holder.btAddToCart.getText().toString() == "REMOVE FROM CART"){
                CartWrapper cartWrapper = new CartWrapper();
                cartWrapper.setType(inventoryItem.getType());
                cartWrapper.setPid(inventoryItem.getId());
                cartWrapper.setDid(inventoryItem.getDid());

                SharedPreferences sharedPreferences2 = holder.rootView.getContext().getSharedPreferences("com.example.musichum", Context.MODE_PRIVATE);

                Call<Void> response = iApiCalls.deleteFromCart(sharedPreferences2.getString("isLoggedIn", ""), cartWrapper);

                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            holder.btAddToCart.setText("ADD TO CART");
                        }

                        else{
                            holder.btAddToCart.setText("FAILED");

                            try {
                                wait(5000);
                                holder.btAddToCart.setText("REMOVE FROM CART");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(v.getContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    public interface InventoryItemInterface{
        void onUserClick(InventoryItem inventoryItem);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rootView;
        private final TextView tvCost;
        private final TextView tvDistName;
        private final Button btAddToCart;

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvDistName = view.findViewById(R.id.tv_distName);
            tvCost = view.findViewById(R.id.tv_cost);
            btAddToCart = view.findViewById(R.id.bt_addToCart);

        }
    }

}
