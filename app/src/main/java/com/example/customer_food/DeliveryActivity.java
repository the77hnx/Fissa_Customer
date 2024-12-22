package com.example.customer_food;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.customer_food.DBHelper.DBHelper;
import com.example.customer_food.Model.FoodItem;
import com.example.customer_food.Model.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {

    private TextView commentMlTextView;
    private TextView posScoreMlTextView;
    private TextView productPriceTextView;
    private TextView deliveryPriceTextView;
    private TextView totalWithDeliveryTextView;
    private EditText textAdditionalDescriptionmag, textAdditionalDescriptionliv;
    private Button confirmButton;
    int customerId , deliveryWorkerId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_delivery);

        // Initialize Views
        commentMlTextView = findViewById(R.id.namerestaurant);
        posScoreMlTextView = findViewById(R.id.pos_score_ml);
        productPriceTextView = findViewById(R.id.productPriceTextView_del);
        deliveryPriceTextView = findViewById(R.id.deliveryPriceTextView_del);
        totalWithDeliveryTextView = findViewById(R.id.totalPriceTextView_del);
        textAdditionalDescriptionmag = findViewById(R.id.textAdditionalDescriptionmag);
        textAdditionalDescriptionliv = findViewById(R.id.textAdditionalDescriptionliv);
        confirmButton = findViewById(R.id.confirmButton);

        // Retrieve data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeViews(extras);
        }

        // Apply WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle Confirm Button click
        confirmButton.setOnClickListener(v -> confirmOrder(extras));
    }

    private void initializeViews(Bundle extras) {
        String restaurantName = extras.getString("restaurantName", "");
        String orderCount = extras.getString("orderCount", "");
        String totalPriceString = extras.getString("totalPrice", "0");
        String deliveryPriceString = extras.getString("deliveryPrice", "0");
        String totalWithDeliveryString = extras.getString("totalWithDelivery", "0");

        commentMlTextView.setText("المطعم : " + restaurantName);
        posScoreMlTextView.setText("عدد الأطباق : " + orderCount);
        productPriceTextView.setText("سعر الطلب : " + totalPriceString + " دج");
        deliveryPriceTextView.setText("سعر التوصيل : " + deliveryPriceString + " دج");
        totalWithDeliveryTextView.setText("السعر الكلي : " + totalWithDeliveryString + " دج");
    }

    private void confirmOrder(Bundle extras) {
        String additionalInfomag = textAdditionalDescriptionmag.getText().toString().trim();
        String additionalInfoliv = textAdditionalDescriptionliv.getText().toString().trim();

        if (validateInputs(additionalInfomag, additionalInfoliv)) {
            ArrayList<FoodItem> foodItems = getIntent().getParcelableArrayListExtra("foodItems");
            String restaurantName = extras.getString("restaurantName", "");
            String totalPriceString = extras.getString("totalPrice", "0");
            String deliveryPriceString = extras.getString("deliveryPrice", "0");

            double totalPrice = Double.parseDouble(totalPriceString);
            double deliveryPrice = Double.parseDouble(deliveryPriceString);
            String orderStatus = "Pending";
            int orderId = getUserOrderNumber();
            DBHelper dbHelper = new DBHelper(this);

            String userId = dbHelper.getUserId();
            Log.d("user id = ", userId) ;
            customerId = Integer.parseInt(userId);

            Order newOrder = new Order(orderId, restaurantName, deliveryPrice, totalPrice, "", "", orderStatus, additionalInfomag, additionalInfoliv);
            sendOrderToServer(customerId, deliveryWorkerId, newOrder, additionalInfomag, additionalInfoliv, foodItems);
            showConfirmationDialog(newOrder);
        }
    }

    private boolean validateInputs(String additionalInfomag, String additionalInfoliv) {
        if (additionalInfomag.isEmpty() && additionalInfoliv.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال معلومات إضافية", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showConfirmationDialog(Order order) {
        new AlertDialog.Builder(DeliveryActivity.this)
                .setTitle("تأكيد الطلب")
                .setMessage("هل أنت متأكد من عملية الدفع")
                .setPositiveButton("OK", (dialog, which) -> showOrderConfirmedDialog(order))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showOrderConfirmedDialog(Order order) {
        new AlertDialog.Builder(DeliveryActivity.this)
                .setTitle("تأكيد الطلب")
                .setMessage("شكرا على ثقتك سيدي الزبون")
                .setPositiveButton("موافق", (dialog, which) -> {
                    Intent intent = new Intent(DeliveryActivity.this, HistoryOrdersActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    private int getUserOrderNumber() {
        return 12345; // Replace with your logic to generate unique order numbers
    }

    private void sendOrderToServer(int customerId, int deliveryWorkerId, Order order, String additionalInfomag, String additionalInfoliv, ArrayList<FoodItem> foodItems) {
        String url = "https://www.fissadelivery.com/fissa/Customer/Add_demande.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Try to parse the response as JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(DeliveryActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeliveryActivity.this, "Failed to add order: " + message, Toast.LENGTH_SHORT).show();
                            Log.e("DeliveryActivity", "Error adding order: " + message);
                        }
                    } catch (JSONException e) {
                        // If parsing fails, treat the response as a plain string
                        Toast.makeText(DeliveryActivity.this, "Response: " + response, Toast.LENGTH_SHORT).show();
                        Log.e("DeliveryActivity", "Error adding order: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(DeliveryActivity.this, "Error adding order: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customerId", String.valueOf(customerId));
                params.put("deliveryWorkerId", String.valueOf(deliveryWorkerId));
                params.put("restaurantName", order.getStoreName());
                params.put("orderCount", String.valueOf(foodItems.size())); // Correctly passing order count
                params.put("totalPrice", String.valueOf(order.getOrderPrice()));
                params.put("deliveryPrice", String.valueOf(order.getDeliveryPrice()));
                params.put("orderStatus", "1"); // Order status ID (initially 1)

                // Add food items to parameters
                for (int i = 0; i < foodItems.size(); i++) {
                    FoodItem foodItem = foodItems.get(i);
                    params.put("product_name_" + i, foodItem.getName());
                    params.put("product_price_" + i, String.valueOf(foodItem.getPrice()));
                    params.put("product_quantity_" + i, String.valueOf(foodItem.getCount()));
                }

                params.put("additionalInfomag", additionalInfomag);
                params.put("additionalInfoliv", additionalInfoliv);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

}
