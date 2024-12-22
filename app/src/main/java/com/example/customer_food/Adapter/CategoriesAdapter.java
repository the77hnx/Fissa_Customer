package com.example.customer_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer_food.R;
import com.example.customer_food.Model.CategoriesItem;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> implements Filterable {

    private List<CategoriesItem> categoriesList;
    private List<CategoriesItem> categoriesListFull;
    private OnCategoryClickListener listener;
    private Context context;

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName, String categoryId);
    }

    public CategoriesAdapter(Context context, List<CategoriesItem> categoriesList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.categoriesListFull = new ArrayList<>(categoriesList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_cat, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoriesItem category = categoriesList.get(position);
        holder.categoryName.setText(category.getCategoryName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category.getCategoryName(), category.getCategoryId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CategoriesItem> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(categoriesListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (CategoriesItem item : categoriesListFull) {
                        if (item.getCategoryName().toLowerCase().contains(filterPattern)) {
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
                categoriesList.clear();
                categoriesList.addAll((List<CategoriesItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoriesViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.card_text);
        }
    }
}
