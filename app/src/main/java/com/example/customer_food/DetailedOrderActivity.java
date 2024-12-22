package com.example.customer_food;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.customer_food.Adapter.ItemDetailsAdpter;
import com.example.customer_food.Model.FoodItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailedOrderActivity extends AppCompatActivity {

    // View declarations
    private TextView additionalInfoMagView, additionalInfoLivView;
    private TextView restaurantNameView, restaurantLocationView, restaurantStatusView, restaurantRatingView, number_items;
    private ImageView restaurantImageView, deliveryWorkerImageView;
    private Button delivery_order_done;
    private TextView deliveryWorkerNameView;
    private RecyclerView itemRecyclerView;
    private ItemDetailsAdpter itemAdapter;
    private List<FoodItem> foodItemsList;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the title bar and make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detailed_order);

        // Initialize views
        initializeViews();

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Initialize RecyclerView
        foodItemsList = new ArrayList<>();
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemDetailsAdpter(this, foodItemsList);
        itemRecyclerView.setAdapter(itemAdapter);

        // Get order ID from intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int orderId = extras.getInt("orderId", -1);
            Log.d(TAG, "Order ID received: " + orderId);
            if (orderId != -1) {
                fetchOrderDetails(orderId);
            } else {
                showError("Invalid order ID.");
            }
        } else {
            showError("No order ID provided.");
        }

        delivery_order_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus();
            }
        });
    }

    private void initializeViews() {
        additionalInfoMagView = findViewById(R.id.additional_information_text);
        additionalInfoLivView = findViewById(R.id.additional_information_text_mandel);
        restaurantNameView = findViewById(R.id.resnameinfodet);
        restaurantLocationView = findViewById(R.id.placeresinfodet);
        restaurantStatusView = findViewById(R.id.statusinfodet);
        restaurantRatingView = findViewById(R.id.valtvinfodet);
        restaurantImageView = findViewById(R.id.imageresinfodet);
        deliveryWorkerNameView = findViewById(R.id.name_user_det);
        deliveryWorkerImageView = findViewById(R.id.user_image);
        number_items = findViewById(R.id.number_items);
        delivery_order_done = findViewById(R.id.delivery_order_done);
    }

    private void fetchOrderDetails(int orderId) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("Id_Demandes", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
            showError("Failed to create request parameters.");
            return;
        }

        Request request = new Request.Builder()
                .url("https://www.fissadelivery.com/fissa/Customer/Details_order.php")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams.toString()))
                .build();

        Log.d(TAG, "Order ID sent: " + orderId);
        Log.d(TAG, "Request Body: " + jsonParams.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request failed: " + e.getMessage());
                runOnUiThread(() -> showError("Failed to fetch order details."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    Log.d(TAG, "Response from server: " + jsonResponse);
                    runOnUiThread(() -> handleResponse(jsonResponse));
                } else {
                    Log.e(TAG, "Server error: " + response.message());
                    runOnUiThread(() -> showError("Error: " + response.message()));
                }
            }
        });
    }

    private void handleResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (!jsonObject.getBoolean("error")) {
                JSONObject order = jsonObject.getJSONObject("order");
                updateOrderDetails(order);
            } else {
                String errorMessage = jsonObject.getString("message");
                showError("Error fetching order data: " + errorMessage);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse response: " + e.getMessage());
            showError("Received unexpected response from server.");
        }
    }

    private void updateOrderDetails(JSONObject order) throws JSONException {
        additionalInfoMagView.setText(order.optString("additional_info_magasin", "N/A"));
        additionalInfoLivView.setText(order.optString("additional_info_livreur", "N/A"));

        restaurantNameView.setText(order.optString("restaurant_name", "N/A"));
        restaurantLocationView.setText(order.optString("restaurant_address", "N/A"));
        restaurantStatusView.setText(order.optString("restaurant_status", "N/A"));
        restaurantRatingView.setText(order.optString("restaurant_eval", "N/A"));

        deliveryWorkerNameView.setText(order.optString("delivery_worker_name", "N/A"));

        // Load restaurant image
        String restaurantImagePath = order.optString("restaurant_image_path", null);
        String baseUrl = "https://www.fissadelivery.com/fissa/";
        String fullrestaurantImagePath = baseUrl + restaurantImagePath.replace("../", "");

        Glide.with(this)
                .load(fullrestaurantImagePath)
                .into(restaurantImageView);

        // Load delivery worker image
        String deliveryWorkerImagePath = order.optString("delivery_worker_image_path", null);

        String fulldeliveryWorkerImagePath = baseUrl + deliveryWorkerImagePath.replace("../", "");
        Glide.with(this)
                .load(fulldeliveryWorkerImagePath)
                .into(deliveryWorkerImageView);

        // Get order items if included in the response
        if (order.has("items")) {
            JSONArray items = order.getJSONArray("items");
            foodItemsList.clear(); // Clear existing items

            int SumitemQuantity = 0; // Declare and initialize outside the loop
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String itemName = item.optString("itemName");
                double itemPrice = item.optDouble("itemPrice");
                int itemQuantity = item.optInt("itemQuantity");
                String itemImage = item.optString("itemImage"); // Get the image path

                SumitemQuantity += itemQuantity; // Accumulate item quantities
                foodItemsList.add(new FoodItem(itemName, itemPrice, itemQuantity, itemImage));
            }
            number_items.setText("عدد العناصر : " + SumitemQuantity);
            itemAdapter.notifyDataSetChanged(); // Notify the adapter about data changes
        }
    }

    private void showError(String message) {
        Log.e(TAG, message);
        // Implement error display logic (e.g., Toast or Snackbar)
        // For example:
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Method to update order status
    private void updateOrderStatus() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int orderId = extras.getInt("orderId", -1);
            if (orderId != -1) {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("Id_Demandes", orderId);
                    jsonParams.put("Id_Statut_Commande", 6); // Set new status
                } catch (JSONException e) {
                    e.printStackTrace();
                    showError("Failed to create request parameters.");
                    return;
                }

                Request request = new Request.Builder()
                        .url("https://www.fissadelivery.com/fissa/Customer/Details_order.php")
                        .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams.toString()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Network request failed: " + e.getMessage());
                        runOnUiThread(() -> showError("Failed to update order status."));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                Log.d(TAG, "Order status updated successfully.");
                                Toast.makeText(DetailedOrderActivity.this, "شكرك على حسن ثقتك", Toast.LENGTH_LONG).show();
                                // You can show a success message or navigate to another screen
                            });
                        } else {
                            Log.e(TAG, "Server error: " + response.message());
                            runOnUiThread(() -> showError("Error: " + response.message()));
                        }
                    }
                });
            } else {
                showError("Invalid order ID.");
            }
        } else {
            showError("No order ID provided.");
        }
    }
}
