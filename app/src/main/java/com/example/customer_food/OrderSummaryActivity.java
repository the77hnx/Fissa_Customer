package com.example.customer_food;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer_food.Adapter.OrderSummaryAdapter;
import com.example.customer_food.Model.FoodItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class OrderSummaryActivity extends AppCompatActivity {

    private TextView totalTextView;
    private TextView deliveryPriceTextView;
    private TextView totalWithDeliveryTextView;
    private Button buyButton;
    private RecyclerView recyclerView;
    private ArrayList<FoodItem> foodItems;
    private OrderSummaryAdapter adapter;
    private String restaurantName;
    private String restaurantEvaluation;
    private String restaurantLocation;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_order_summary);

        totalTextView = findViewById(R.id.totalTextView);
        deliveryPriceTextView = findViewById(R.id.deliveryPriceTextView);
        totalWithDeliveryTextView = findViewById(R.id.totalWithDeliveryTextView);
        buyButton = findViewById(R.id.buyButton);
        recyclerView = findViewById(R.id.recyclerViewforordersummary);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_os);
        bottomNavigationView.setSelectedItemId(R.id.navigation_basket); // Change this based on the activity

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Navigate to ShopsActivity
                    startActivity(new Intent(OrderSummaryActivity.this, ShopsActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_basket) {
                    // Navigate to OrderSummaryActivity
                    Toast.makeText(OrderSummaryActivity.this, "لم تضع اي شيء في السلة", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.navigation_orders) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(OrderSummaryActivity.this, HistoryOrdersActivity.class));
                    return true;
                }
                return false;
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            foodItems = intent.getParcelableArrayListExtra("filteredItems");
            restaurantName = intent.getStringExtra("restaurantName");
            restaurantEvaluation = intent.getStringExtra("restaurantEvaluation");
            restaurantLocation = intent.getStringExtra("restaurantLocation");
            value = intent.getStringExtra("val");
        }

        // Set up RecyclerView
        initRecyclerView();

        calculateTotalPrice();

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderSummaryActivity.this, DeliveryActivity.class);
                intent.putExtra("restaurantName", restaurantName);
                intent.putExtra("orderCount", foodItems.size() + " أطباق");
                intent.putExtra("totalPrice", extractPriceFromString(totalTextView.getText().toString()));
                intent.putExtra("deliveryPrice", extractPriceFromString(deliveryPriceTextView.getText().toString()));
                intent.putExtra("totalWithDelivery", extractPriceFromString(totalWithDeliveryTextView.getText().toString()));
                intent.putParcelableArrayListExtra("foodItems", foodItems);

                startActivity(intent);
            }
        });
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;

        if (foodItems != null) {
            for (FoodItem item : foodItems) {
                if (item.getCount() > 0) {
                    totalPrice += item.getPrice() * item.getCount();
                }
            }
        }


        totalTextView.setText("السعر : " + totalPrice + " دج");

        double deliveryPrice = fetchDeliveryPrice();
        deliveryPriceTextView.setText("سعر التوصيل : " + deliveryPrice + " دج");

        double totalWithDelivery = totalPrice + deliveryPrice;
        totalWithDeliveryTextView.setText("السعر الكلي : " + totalWithDelivery + " دج");
    }
    private double fetchDeliveryPrice() {
        return 20.0;  // Replace with your actual logic to fetch delivery price
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new OrderSummaryAdapter(foodItems, this::calculateTotalPrice);
        recyclerView.setAdapter(adapter);
    }
    private String extractPriceFromString(String priceString) {
        // Extract numeric part from the string
        return priceString.replaceAll("[^0-9.]", ""); // Keep only digits and decimal points
    }

}
