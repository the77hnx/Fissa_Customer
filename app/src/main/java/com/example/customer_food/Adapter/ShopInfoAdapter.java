package com.example.customer_food.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.customer_food.Model.FoodItem;
import com.example.customer_food.R;

import java.util.ArrayList;
import java.util.Locale;

public class ShopInfoAdapter extends RecyclerView.Adapter<ShopInfoAdapter.ViewHolder> {

    private ArrayList<FoodItem> foodItemList;
    private final Runnable updateTotalPriceCallback;

    public ShopInfoAdapter(ArrayList<FoodItem> foodItemList, Runnable updateTotalPriceCallback) {
        this.foodItemList = foodItemList;
        this.updateTotalPriceCallback = updateTotalPriceCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_add_remove, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodPrice.setText(foodItem.getPrice() + " دج");
        holder.foodCount.setText(String.valueOf(foodItem.getCount()));

        holder.allProductPrice.setText(String.format(Locale.ENGLISH, "%.2f دج", foodItem.getPrice() * foodItem.getCount()));

        String baseUrl = "https://www.fissadelivery.com/fissa/";
        String fullImagePath = baseUrl + foodItem.getImageUrl().replace("../", "");

        // Load image using Glide
        Glide.with(holder.foodImageView.getContext())
                .load(fullImagePath) // Load the image URL
                .into(holder.foodImageView);


        holder.addButton.setOnClickListener(v -> {
            foodItem.incrementCount();
            holder.foodCount.setText(String.valueOf(foodItem.getCount()));
            holder.allProductPrice.setText(String.format(Locale.ENGLISH, "%.2f دج", foodItem.getPrice() * foodItem.getCount()));
            updateTotalPriceCallback.run();
        });

        holder.removeButton.setOnClickListener(v -> {
            foodItem.decrementCount();
            holder.foodCount.setText(String.valueOf(foodItem.getCount()));
            holder.allProductPrice.setText(String.format(Locale.ENGLISH, "%.2f دج", foodItem.getPrice() * foodItem.getCount()));
            updateTotalPriceCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, foodCount, allProductPrice;
        Button addButton, removeButton;
        ImageView foodImageView; // Add ImageView


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.productNameTextView_frag);
            foodPrice = itemView.findViewById(R.id.productPriceTextView_frag);
            foodCount = itemView.findViewById(R.id.food_details_item_count);
            foodImageView = itemView.findViewById(R.id.imageitem); // Initialize ImageView
            addButton = itemView.findViewById(R.id.food_details_food_add);
            removeButton = itemView.findViewById(R.id.food_details_food_remove);
            allProductPrice = itemView.findViewById(R.id.allproductPriceTextView_frag); // Initialize here

        }
    }
}