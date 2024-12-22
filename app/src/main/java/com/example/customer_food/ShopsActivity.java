package com.example.customer_food;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer_food.Adapter.CategoriesAdapter;
import com.example.customer_food.Adapter.RestaurantAdapter;
import com.example.customer_food.Model.CategoriesItem;
import com.example.customer_food.Model.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPop;
    private RecyclerView recyclerViewCat;
    private RestaurantAdapter popularRestaurantsAdapter;
    private CategoriesAdapter categoriesAdapter;
    private EditText searchEditText;
    private List<Restaurant> restaurantList;
    private List<CategoriesItem> categoryList;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shops);

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_shops);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ShopsActivity.this, ShopsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_following) {
                Toast.makeText(ShopsActivity.this, "لم تتابع اي شيء بعد", Toast.LENGTH_SHORT).show();
                return false;
            } else if (itemId == R.id.navigation_basket) {
                startActivity(new Intent(ShopsActivity.this, HistoryOrdersActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(ShopsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Initialize RecyclerViews
        recyclerViewPop = findViewById(R.id.popularRestaurantsRecyclerViewver);
        recyclerViewCat = findViewById(R.id.popularRestaurantsRecyclerViewhor);

        // Initialize searchEditText
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initialize OkHttpClient and lists
        client = new OkHttpClient();
        restaurantList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Setup RecyclerViews
        setupPopularRestaurantsRecyclerView();
        setupCategoriesRecyclerView();

        // Fetch restaurants and categories data
        fetchRestaurantAndCategoryData();
    }

    private void setupPopularRestaurantsRecyclerView() {
        recyclerViewPop.setLayoutManager(new LinearLayoutManager(this));
        popularRestaurantsAdapter = new RestaurantAdapter(this, restaurantList, restaurant -> {
            // Handle restaurant item click
            Intent intent = new Intent(ShopsActivity.this, ShopInfoActivity.class);
            intent.putExtra("RESTAURANT_ID", restaurant.getRestaurantId());
            startActivity(intent);
        });
        recyclerViewPop.setAdapter(popularRestaurantsAdapter);
    }

    private void setupCategoriesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCat.setLayoutManager(layoutManager);

        // Initialize the categories adapter
        categoriesAdapter = new CategoriesAdapter(this, categoryList, (categoryName, categoryId) -> {
//            filterRestaurantsByCategory(categoryId);
        });
        recyclerViewCat.setAdapter(categoriesAdapter);
    }

    private void filterData(String searchText) {
        popularRestaurantsAdapter.getFilter().filter(searchText);
    }

//    private void filterRestaurantsByCategory(String categoryId) {
//        List<Restaurant> filteredList = new ArrayList<>();
//
//        for (Restaurant restaurant : restaurantList) {
//            // Check if the restaurant's category ID matches the selected category ID
//            if (restaurant.getCategoryId().equals(categoryId)) {
//                filteredList.add(restaurant);
//            }
//        }
//
//        // Update the adapter with the filtered list
//        popularRestaurantsAdapter.updateData(filteredList);
//    }

    private void fetchRestaurantAndCategoryData() {
        String url = "https://www.fissadelivery.com/fissa/Customer/Fetch_restaurants.php";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ShopsActivity.this, "Failed to fetch data", Toast.LENGTH_LONG).show());
                Log.e("ShopsActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Log.d("ShopsActivity", "Response JSON: " + jsonData);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray restaurantArray = jsonObject.getJSONArray("restaurants");
                        JSONArray categoryArray = jsonObject.getJSONArray("items");

                        restaurantList.clear();
                        categoryList.clear();

                        // Parse restaurant data
                        for (int i = 0; i < restaurantArray.length(); i++) {
                            JSONObject restaurantObject = restaurantArray.getJSONObject(i);
                            String restaurantId = restaurantObject.getString("restaurantId");
                            String restaurantName = restaurantObject.getString("restaurantName");
                            String restaurantLocation = restaurantObject.getString("restaurantLocation");
                            String restaurantValue = restaurantObject.getString("restaurantValue");
                            String restaurantStatus = restaurantObject.getString("restaurantStatus");
                            String restaurantImage = restaurantObject.getString("restaurantImage");

                            Restaurant restaurant = new Restaurant(restaurantId, restaurantName, restaurantLocation, restaurantValue, restaurantStatus, restaurantImage);
                            restaurantList.add(restaurant);
                        }

                        // Parse category data
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject categoryObject = categoryArray.getJSONObject(i);
                            String categoryName = categoryObject.getString("categoryName");
                            String categoryId = categoryObject.getString("categoryId"); // Ensure this is correctly obtained
                            CategoriesItem category = new CategoriesItem(categoryName, categoryId);
                            categoryList.add(category);
                        }

                        runOnUiThread(() -> {
                            popularRestaurantsAdapter.notifyDataSetChanged();
                            categoriesAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(ShopsActivity.this, "Failed to parse data", Toast.LENGTH_LONG).show());
                        Log.e("ShopsActivity", "JSON Parsing error: " + e.getMessage());
                    }
                }
            }
        });
    }
}