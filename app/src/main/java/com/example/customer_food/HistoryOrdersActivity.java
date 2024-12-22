package com.example.customer_food;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.customer_food.Adapter.HistoryOrderAdapter;
import com.example.customer_food.DBHelper.DBHelper;
import com.example.customer_food.Model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class HistoryOrdersActivity extends AppCompatActivity {

    private static final String TAG = "HistoryOrdersActivity";
    private RecyclerView recyclerView;
    private HistoryOrderAdapter adapter;
    private List<Order> orderList;
    private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_history_orders);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_ho);
        bottomNavigationView.setSelectedItemId(R.id.navigation_orders);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(HistoryOrdersActivity.this, ShopsActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_basket) {
                    Toast.makeText(HistoryOrdersActivity.this, "لم تضع اي شيء بعد في السلة", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.navigation_orders) {
                    startActivity(new Intent(HistoryOrdersActivity.this, HistoryOrdersActivity.class));
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewOrders);
        orderList = new ArrayList<>();
        adapter = new HistoryOrderAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        client = new OkHttpClient();

        DBHelper dbHelper = new DBHelper(this);
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;

        fetchOrders(userId);
    }

    private void fetchOrders(String userId) {
        String url = "https://www.fissadelivery.com/fissa/Customer/History_orders.php"; // Ensure this URL is correct

        RequestBody postUserID = new FormBody.Builder()
                .add("user_id", userId) // Use 'user_id' instead of 'userId'
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(postUserID)
                .build();

        Log.d("request", String.valueOf(request));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse the JSON response
                    String jsonResponse = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray ordersArray = jsonObject.getJSONArray("orders"); // Get orders from the response
                            orderList.clear(); // Clear the existing orders
                            for (int i = 0; i < ordersArray.length(); i++) {
                                JSONObject jsonOrder = ordersArray.getJSONObject(i);

                                // Extract the data from the JSON object
                                int orderId = jsonOrder.getInt("orderId");
                                String restaurantName = jsonOrder.getString("storeName");
                                double deliveryPrice = jsonOrder.optDouble("deliveryPrice", 0.0);
                                double totalPrice = jsonOrder.optDouble("orderPrice", 0.0);
                                String orderDate = jsonOrder.getString("orderDate");
                                String orderTime = jsonOrder.getString("orderTime");
                                String orderStatus = jsonOrder.getString("statusName");
                                String additionalInfomag = jsonOrder.optString("additionalInfomag", "N/A");
                                String additionalInfoliv = jsonOrder.optString("additionalInfoliv", "N/A");

                                // Create an Order object and add it to the order list
                                Order order = new Order(orderId, restaurantName, deliveryPrice, totalPrice, orderDate, orderTime, orderStatus, additionalInfomag, additionalInfoliv);
                                orderList.add(order);
                            }

                            // Notify the adapter about the data changes
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(HistoryOrdersActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(HistoryOrdersActivity.this, "Response was not successful", Toast.LENGTH_SHORT).show();
                    });
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HistoryOrdersActivity.this, "Failed to fetch user data", Toast.LENGTH_LONG).show();
                });
                Log.e("ProfileActivity", "Error: " + e.getMessage());
            }

        });
    }


}
