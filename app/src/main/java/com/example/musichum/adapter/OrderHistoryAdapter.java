package com.example.musichum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichum.R;
import com.example.musichum.models.OrderHistory;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final List<OrderHistory> orderHistories;
    OrderItemInterface orderItemInterface;

    public OrderHistoryAdapter(List<OrderHistory> orderHistories, OrderItemInterface itemInterface){
        this.orderHistories = orderHistories;
        this.orderItemInterface = itemInterface;
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
        OrderHistory orderHistory = orderHistories.get(position);
        holder.tvOrderID.setText(orderHistory.getId());
        holder.tvTimestamp.setText((CharSequence) orderHistory.getTimestamp());
        holder.tvOrders.setText(orderHistory.getCartItems().toString());
        holder.tvTotalCost.setText((int) orderHistory.getTotalAmount());
    }

    public interface OrderItemInterface{
        void onUserClick(OrderHistory orderHistory);
    }

    @Override
    public int getItemCount() {
        return orderHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rootView;
        private final TextView tvOrderID;
        private final TextView tvTimestamp;
        private final TextView tvTotalCost;
        private final TextView tvOrders;

        public ViewHolder(View view){
            super(view);
            rootView = view;
            tvOrderID = view.findViewById(R.id.tv_orderId);
            tvTimestamp = view.findViewById(R.id.tv_timestamp);
            tvOrders = view.findViewById(R.id.tv_orders);
            tvTotalCost = view.findViewById(R.id.tv_totalAmount);
        }
    }

}
