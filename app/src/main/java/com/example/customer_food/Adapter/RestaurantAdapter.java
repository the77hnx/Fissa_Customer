package com.example.customer_food.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.customer_food.Model.Restaurant;
import com.example.customer_food.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> implements Filterable {

    private List<Restaurant> restaurantList;
    private List<Restaurant> restaurantListFull;
    private Context context;
    private Consumer<Restaurant> onRestaurantClickCallback;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, Consumer<Restaurant> onRestaurantClickCallback) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.restaurantListFull = new ArrayList<>(restaurantList);
        this.onRestaurantClickCallback = onRestaurantClickCallback;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_shop_info, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.resname.setText(restaurant.getRestaurantName());
        holder.placeres.setText(restaurant.getRestaurantLocation());
        holder.valtv.setText(restaurant.getRestaurantValue());

        // Set status text color based on restaurant status
//        switch (restaurant.getRestaurantStatus()) {
//            case "مفتوح":
//                holder.status.setTextColor(Color.GREEN);
//                break;
//            case "مغلق":
//                holder.status.setTextColor(Color.RED);
//                break;
//            default:
//                holder.status.setTextColor(Color.GRAY); // Default color
//                break;
//        }
        holder.status.setText(restaurant.getRestaurantStatus());

        // Handle restaurant item click
        holder.itemView.setOnClickListener(v -> {
            if (onRestaurantClickCallback != null) {
                onRestaurantClickCallback.accept(restaurant);
            }
        });
        String baseUrl = "https://www.fissadelivery.com/fissa/";
        String fullImagePath = baseUrl + restaurant.getRestaurantImage().replace("../", "");

        Glide.with(context)
                .load(fullImagePath)
                .into(holder.imageViewres);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Restaurant> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(restaurantListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Restaurant item : restaurantListFull) {
                        if (item.getRestaurantName().toLowerCase().contains(filterPattern) ||
                                item.getRestaurantLocation().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                restaurantList.clear();
                restaurantList.addAll((List<Restaurant>) results.values);
                notifyDataSetChanged();
            }
        };
    }
    public void updateData(List<Restaurant> newRestaurantList) {
        this.restaurantList.clear();
        this.restaurantList.addAll(newRestaurantList);
        notifyDataSetChanged();
    }


    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView resname, placeres, status, valtv;
        ImageView imageViewres;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            resname = itemView.findViewById(R.id.resnameinfo);
            placeres = itemView.findViewById(R.id.placeresinfo);
            status = itemView.findViewById(R.id.statusinfo);
            valtv = itemView.findViewById(R.id.valtvinfo);
            imageViewres = itemView.findViewById(R.id.imageresinfo);
        }
    }
}