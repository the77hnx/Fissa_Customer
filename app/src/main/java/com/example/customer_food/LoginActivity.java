package com.example.customer_food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customer_food.DBHelper.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox checkboxRememberMe;
    private Button btnLogin;
    private TextView resendTextView;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login); // Ensure your layout file is named correctly

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        checkboxRememberMe = findViewById(R.id.checkboxRememberMe);
        btnLogin = findViewById(R.id.btnPhoneLogin);
        resendTextView = findViewById(R.id.resendTextView);
        client = new OkHttpClient();

        // Load saved login information if "Remember Me" was checked
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean rememberMe = preferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            etEmail.setText(preferences.getString("email", ""));
            etPassword.setText(preferences.getString("password", ""));
            checkboxRememberMe.setChecked(true);
        }

        // Set up login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateInput(email, password)) {
                    // Create a POST request to send email and password
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url("https://www.fissadelivery.com/fissa/Customer/Login.php")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            // Handle failure in network call
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Failed to connect to server", Toast.LENGTH_LONG).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String jsonData = response.body().string();
                                Log.d("Server Response", jsonData); // طباعة الرد على الـ logcat

                                try {
                                    JSONObject jsonObject = new JSONObject(jsonData);
                                    boolean success = jsonObject.getBoolean("success");
                                    String message = jsonObject.getString("message");

                                    runOnUiThread(() -> {
                                        if (success) {
                                            // تحقق من وجود "userId" في الرد
                                            String userId = null;
                                            if (jsonObject.has("userId")) {
                                                // حفظ معرف المستخدم في قاعدة البيانات المحلية
                                                try {
                                                    userId = jsonObject.getString("userId");
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                DBHelper dbHelper = new DBHelper(LoginActivity.this);
                                                dbHelper.insertUserId(userId);
                                            } else {
                                                // إذا لم يكن معرف المستخدم موجودًا في الرد
                                                Toast.makeText(LoginActivity.this, "User ID not found in server response", Toast.LENGTH_LONG).show();
                                            }
                                            // If login is successful, navigate to the LocationActivity
                                            Log.d("use id = ", userId);
                                            Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Show error message from server
                                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (JSONException e) {
                                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error parsing server response", Toast.LENGTH_LONG).show());
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show());
                                Log.e("Error : " , response.message());
                            }
                        }


                    });
                }
            }
        });

        // Set up click listener for sign-up navigation
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Validate email and password input
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "الرجاء إدخال البريد الالكتروني", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "الرجاء إدخال كلمة المرور", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Here you can add more checks (e.g., email format, password strength, etc.)
        return true;
    }


}
