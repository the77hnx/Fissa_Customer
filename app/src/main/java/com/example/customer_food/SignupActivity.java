package com.example.customer_food;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullNames, etEmails, etPhoneNumbers, etPassword, etRepassword;
    private Button btnPhoneLogin;
    private TextView resendTextView;
    private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup); // Use the appropriate layout file name here

        // Initialize views
        etFullNames = findViewById(R.id.etfullnames);
        etEmails = findViewById(R.id.etEmails);
        etPhoneNumbers = findViewById(R.id.etPhoneNumbers);
        etPassword = findViewById(R.id.etpassword);
        etRepassword = findViewById(R.id.etrepassword);
        btnPhoneLogin = findViewById(R.id.btnPhoneLogins);
        resendTextView = findViewById(R.id.resendTextView);

        client = new OkHttpClient();


        // Set button click listener
        btnPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set text view click listener for "Sign in" navigation
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login activity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close this activity
            }
        });
    }

    // Method to register user (basic validation)
    private void registerUser() {
        String fullNames = etFullNames.getText().toString().trim();
        String email = etEmails.getText().toString().trim();
        String phoneNumber = etPhoneNumbers.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repassword = etRepassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullNames)) {
            etFullNames.setError("الرجاء إدخال الاسم الكامل");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmails.setError("الرجاء إدخال البريد الإلكتروني");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 10) {
            etPhoneNumbers.setError("الرجاء إدخال رقم هاتف صحيح");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("الرجاء إدخال كلمة السر");
            return;
        }

        if (!password.equals(repassword)) {
            etRepassword.setError("كلمات السر غير متطابقة");
            return;
        }

        insertUserData(fullNames,email,phoneNumber,password);
        // If everything is correct, proceed (e.g., make API call or save to database)

    }

    private void insertUserData(String fullName, String email, String phone, String password) {
        String url = "https://www.fissadelivery.com/fissa/Customer/Signup_User.php";

        // Create a form body with the user data
        RequestBody formBody = new FormBody.Builder()
                .add("full_name", fullName)
                .add("email", email)
                .add("phone", phone)
                .add("password", password)
                .build();

        // Build the POST request
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "Failed to insert user data", Toast.LENGTH_LONG).show();
                });
                Log.e("SignupActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    String jsonData = response.body().string();
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();

                        // Optionally, redirect to another activity (like HomeActivity)
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // Handle unsuccessful response
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "Error signing up", Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

}
