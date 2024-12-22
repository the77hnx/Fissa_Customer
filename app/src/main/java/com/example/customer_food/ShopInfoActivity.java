package com.example.customer_food;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.customer_food.Adapter.ShopInfoAdapter;
import com.example.customer_food.Model.FoodItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class ShopInfoActivity extends AppCompatActivity {

    private TextView textRestaurantName, textEvaluation, placeRes, val, timedel;
    private ImageView imageres;
    private Button share, totalPriceButton;
    private RecyclerView recyclerView;
    private ShopInfoAdapter shopInfoAdapter;
    private ArrayList<FoodItem> categoryList;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shop_info);

        // Initialize views
        imageres = findViewById(R.id.imageres);
        textRestaurantName = findViewById(R.id.text_restaurant_name);
        textEvaluation = findViewById(R.id.text_evaluation);
        placeRes = findViewById(R.id.placeres_shopinfo);
        val = findViewById(R.id.val);
        timedel = findViewById(R.id.timedel);
        share = findViewById(R.id.button);
        totalPriceButton = findViewById(R.id.totalPriceButton);
        recyclerView = findViewById(R.id.recyclerView); // Initialize RecyclerView

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_sinfo);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // Change this based on the activity

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Navigate to ShopsActivity
                    startActivity(new Intent(ShopInfoActivity.this, ShopsActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_basket) {
                    // Navigate to OrderSummaryActivity
                    Toast.makeText(ShopInfoActivity.this, "لم تضع اي شيء في السلة", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.navigation_orders) {
                    // Navigate to HistoryOrdersActivity
                    startActivity(new Intent(ShopInfoActivity.this, HistoryOrdersActivity.class));
                    return true;
                }
                return false;
            }
        });

        // Initialize the category list and adapter in onCreate
        categoryList = new ArrayList<>();
        Runnable updateTotalPriceCallback = this::updateTotalPrice;
        shopInfoAdapter = new ShopInfoAdapter(categoryList, updateTotalPriceCallback);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(shopInfoAdapter);

        // Set click listener for the total price button
        totalPriceButton.setOnClickListener(v -> {
            ArrayList<FoodItem> filteredItems = new ArrayList<>();
            for (FoodItem item : categoryList) {
                if (item.getCount() > 0) {
                    filteredItems.add(item);
                }
            }
            Intent intent = new Intent(ShopInfoActivity.this, OrderSummaryActivity.class);
            intent.putParcelableArrayListExtra("filteredItems", filteredItems);
            intent.putExtra("restaurantName", textRestaurantName.getText().toString());
            intent.putExtra("restaurantEvaluation", textEvaluation.getText().toString());
            intent.putExtra("restaurantLocation", placeRes.getText().toString());

            startActivity(intent);
        });

        // Get restaurant ID from Intent and fetch data from the server
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String restaurantId = extras.getString("RESTAURANT_ID", "");

            // Fetch restaurant products using the restaurant ID
            fetchRestaurantProducts(restaurantId);
        }
    }

    private void fetchRestaurantProducts(String restaurantId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String result = "";
                try {
                    URL url = new URL("https://www.fissadelivery.com/fissa/Customer/Magasin_Info.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "restaurantId=" + URLEncoder.encode(params[0], "UTF-8");

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        result = sb.toString();
                        reader.close();
                    } else {
                        result = "Error: " + responseCode;
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "Error: " + e.getMessage();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.contains("Error")) {
                    Toast.makeText(ShopInfoActivity.this, "Error fetching data: " + result, Toast.LENGTH_SHORT).show();
                    Log.e("ShopInfoActivity", "Error: " + result);
                } else {
                    // Log the result for debugging
                    Log.d("ShopInfoActivity", "Result: " + result);
                    // Handle the result and update UI
                    handleFetchResult(result);
                }
            }
        }.execute(restaurantId);
    }

    private void handleFetchResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject restaurantObject = jsonObject.getJSONObject("restaurant");
            JSONArray productsArray = jsonObject.getJSONArray("products");

            // Extract restaurant data
            String restaurantName = restaurantObject.getString("Nom_magasin");
            String restaurantEvaluation = restaurantObject.isNull("Evaluation") ? "No evaluation" : restaurantObject.getString("Evaluation");
            String restaurantLocation = restaurantObject.getString("Address_magasin");
            String restaurantStatus = restaurantObject.getString("Statut_magasin");
            String restaurantImage = restaurantObject.getString("Image_path");  // Get restaurant image path

            // Update UI
            textRestaurantName.setText(restaurantName);
            textEvaluation.setText(restaurantEvaluation);
            placeRes.setText(restaurantLocation);
            val.setText("100 DZD"); // Adjust based on your data
            timedel.setText("0:30 - 1:30"); // Adjust based on your data
            // Load the restaurant image into the ImageView using Glide
            String baseUrl = "https://www.fissadelivery.com/fissa/images/";  // Replace with your actual base URL
            String fullImageUrl = baseUrl + restaurantImage;
            Glide.with(this)
                    .load(fullImageUrl)
                    .into(imageres);

            // Ensure categoryList is not null before clearing
            if (categoryList == null) {
                categoryList = new ArrayList<>();
            } else {
                categoryList.clear();
            }

            // Update RecyclerView with products
            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject productObject = productsArray.getJSONObject(i);
                // Using updated keys from PHP response
                FoodItem foodItem = new FoodItem(
                        productObject.getString("productName"),
                        productObject.getDouble("price"),
                        productObject.getInt("count"), // This will be 0 as set in PHP script
                        productObject.getString("productImage") // Fetch the image path
                         );
                categoryList.add(foodItem);
            }
            shopInfoAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ShopInfoActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
            Log.e("ShopInfoActivity", "Error Parsing data: " + e.getMessage());
        }
    }

    private void updateTotalPrice() {
        // Calculate and update total price
        double totalPrice = 0.0;
        for (FoodItem item : categoryList) {
            totalPrice += item.getPrice() * item.getCount();
        }
        totalPriceButton.setText(String.format(Locale.ENGLISH, "الاجمالي: %.2f دج", totalPrice));
    }
}