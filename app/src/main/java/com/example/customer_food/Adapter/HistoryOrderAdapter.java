package com.example.customer_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer_food.DetailedOrderActivity;
import com.example.customer_food.Model.Order;
import com.example.customer_food.R;

import java.util.List;

public class HistoryOrderAdapter extends RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;

    public HistoryOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orderList = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderIdTextView.setText("رقم الطلب #" + order.getOrderId());
        holder.storeNameTextView.setText("المحل : " + order.getStoreName());
        holder.orderPriceTextView.setText("سعر الطلب: " + order.getOrderPrice()+"دج");
        holder.deliveryPriceTextView.setText("سعر التوصيل: " + order.getDeliveryPrice()+"دج");
        holder.orderDateTextView.setText("التاريخ: " + order.getOrderDate());
        holder.orderTimeTextView.setText("الوقت: " + order.getOrderTime());
        holder.statusNameTextView.setText("حالة الطلب: " + order.getStatusName());

        holder.buttonMoreInfo.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailedOrderActivity.class);
            intent.putExtra("orderId", order.getOrderId()); // Pass the order ID as int
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView storeNameTextView;
        TextView orderPriceTextView;
        TextView deliveryPriceTextView;
        TextView orderDateTextView;
        TextView orderTimeTextView;
        TextView statusNameTextView;
        Button buttonMoreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewNumberOfDelivery);
            storeNameTextView = itemView.findViewById(R.id.textViewRestaurantName);
            orderPriceTextView = itemView.findViewById(R.id.textViewOrderPrice);
            deliveryPriceTextView = itemView.findViewById(R.id.textViewDeliveryPrice);
            orderDateTextView = itemView.findViewById(R.id.textViewDateOfDelivery);
            orderTimeTextView = itemView.findViewById(R.id.textViewTimeOfDelivery);
            statusNameTextView = itemView.findViewById(R.id.textViewDeliveryStatus);
            buttonMoreInfo = itemView.findViewById(R.id.buttonMoreInfo);
        }
    }
}
