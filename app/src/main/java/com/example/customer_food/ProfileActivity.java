package com.example.customer_food;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.customer_food.DBHelper.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private TextView displayTextView, descriptionTextView, addressTextView, passwordTextView, editGPStext;
    private EditText etNumber, etName, addressEditText, passwordEditText;
    private Button editBtn1, editBtn2, editBtn3, editBtnPassword, editProfileImageButton, btnSave;
    private ImageView profileImageView;
    private boolean isEditingName = false, isEditingDescription = false, isEditingAddress = false, isEditingPassword = false;
    private OkHttpClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        // Initialize views
        displayTextView = findViewById(R.id.displayTextView);
        descriptionTextView = findViewById(R.id.descriptiontv);
        addressTextView = findViewById(R.id.addresstvwritten);
        editGPStext = findViewById(R.id.editGPStext);
        etNumber = findViewById(R.id.etnumberedit);
        etName = findViewById(R.id.etNameedit);
        addressEditText = findViewById(R.id.addresset);
        passwordEditText = findViewById(R.id.passwordet);
        passwordTextView = findViewById(R.id.passtvwritten);
        editBtn1 = findViewById(R.id.editbtn1);
        editBtn2 = findViewById(R.id.editbtn2);
        editBtn3 = findViewById(R.id.editbtn3);
        editBtnPassword = findViewById(R.id.editbtn4);
        btnSave = findViewById(R.id.saveeditbtn);
        editProfileImageButton = findViewById(R.id.editProfileImageButton);
        profileImageView = findViewById(R.id.profileImageView);
        client = new OkHttpClient();

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_ep);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, ShopsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_following) {
                Toast.makeText(ProfileActivity.this, "لم تتابع اي شيء بعد", Toast.LENGTH_SHORT).show();
                return false;
            } else if (itemId == R.id.navigation_basket) {
                startActivity(new Intent(ProfileActivity.this, HistoryOrdersActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                return true;
            } else {
                return false;
            }
        });

        // Set click listeners for the buttons
        editBtn1.setOnClickListener(v -> {
            toggleEditText(etNumber, displayTextView, editBtn1, isEditingName);
            isEditingName = !isEditingName;
        });

        editBtn2.setOnClickListener(v -> {
            toggleEditText(etName, descriptionTextView, editBtn2, isEditingDescription);
            isEditingDescription = !isEditingDescription;
        });

        editBtn3.setOnClickListener(v -> {
            toggleEditText(addressEditText, addressTextView, editBtn3, isEditingAddress);
            isEditingAddress = !isEditingAddress;
        });

        editBtnPassword.setOnClickListener(v -> {
            toggleEditText(passwordEditText, passwordTextView, editBtnPassword, isEditingPassword);
            isEditingPassword = !isEditingPassword;
        });


        DBHelper dbHelper = new DBHelper(this);
        String userId = dbHelper.getUserId();
        Log.d("user id = ", userId) ;

        editProfileImageButton.setOnClickListener(v -> showImageSourceDialog());

        // Save button listener
        btnSave.setOnClickListener(v -> {
            saveAllEdits();
            String fullName = displayTextView.getText().toString();
            String email = descriptionTextView.getText().toString();
            String phone = addressTextView.getText().toString();
            String password = passwordTextView.getText().toString();
            String address = editGPStext.getText().toString();
            Bitmap profileBitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
            updateUserData(fullName, email, phone, password, address, userId, profileBitmap);
        });

        // Fetch and display user data
        fetchUserData(userId);
    }

    private void toggleEditText(EditText editText, TextView textView, Button button, boolean isEditing) {
        if (isEditing) {
            // Save changes and switch to TextView
            String newText = editText.getText().toString();
            if (!TextUtils.isEmpty(newText)) {
                textView.setText(newText);
            }
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            button.setBackgroundResource(R.drawable.ic_edit);
        } else {
            // Switch to EditText
            editText.setText(textView.getText());
            textView.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            button.setBackgroundResource(R.drawable.ic_done);
        }
    }

    private void saveAllEdits() {
        if (etNumber.getVisibility() == View.VISIBLE) {
            String newNumber = etNumber.getText().toString();
            if (!TextUtils.isEmpty(newNumber)) {
                displayTextView.setText(newNumber);
            }
            etNumber.setVisibility(View.GONE);
            displayTextView.setVisibility(View.VISIBLE);
        }

        if (etName.getVisibility() == View.VISIBLE) {
            String newName = etName.getText().toString();
            if (!TextUtils.isEmpty(newName)) {
                descriptionTextView.setText(newName);
            }
            etName.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.VISIBLE);
        }

        if (addressEditText.getVisibility() == View.VISIBLE) {
            String newAddress = addressEditText.getText().toString();
            if (!TextUtils.isEmpty(newAddress)) {
                addressTextView.setText(newAddress);
            }
            addressEditText.setVisibility(View.GONE);
            addressTextView.setVisibility(View.VISIBLE);
        }

        if (passwordEditText.getVisibility() == View.VISIBLE) {
            String newPassword = passwordEditText.getText().toString();
            if (!TextUtils.isEmpty(newPassword)) {
                passwordTextView.setText(newPassword);  // Avoid showing passwords in plain text in real apps
            }
            passwordEditText.setVisibility(View.GONE);
            passwordTextView.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserData(String fullName, String email, String phone, String password, String address, String userId, Bitmap profileImage) {
        String url = "https://www.fissadelivery.com/fissa/Customer/Update_profile.php"; // Replace with your server's URL

        // Convert the Bitmap to a byte array (optional)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Create a RequestBody for the image
        RequestBody imageBody = RequestBody.create(imageBytes, okhttp3.MediaType.parse("image/jpeg"));

        // Create the multipart request body
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", userId)
                .addFormDataPart("fullName", fullName)
                .addFormDataPart("email", email)
                .addFormDataPart("phone", phone)
                .addFormDataPart("password", password)
                .addFormDataPart("coordonnees", address)
                .addFormDataPart("profile_image", "profile.jpg", imageBody) // Send the image with the name "profile.jpg"
                .build();

        // Build the POST request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e("ProfileActivity", "Error: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Failed to update profile: " + response.message(), Toast.LENGTH_SHORT).show());
                    Log.e("ProfileActivity", "Response Error: " + response.message());
                }
            }
        });
    }

    private void fetchUserData(String userId) {
        String url = "https://www.fissadelivery.com/fissa/Customer/Get_profile.php";

        RequestBody PostuserID = new FormBody.Builder()
                .add("user_id", userId) // استخدام 'user_id' بدلاً من 'userId'
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(PostuserID)
                .build();

        Log.d("request", String.valueOf(request));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_LONG).show();
                });
                Log.e("ProfileActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ProfileActivity", "Raw Response Data: " + responseBody); // Log raw response

                    try {
                        // Check if the response is valid JSON
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("error")) {
                            // Handle error message
                            runOnUiThread(() -> {
                                Toast.makeText(ProfileActivity.this, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                            });
                        } else {
                            // Process user data
                            String fullName = jsonObject.optString("fullName", "N/A");
                            String phone = jsonObject.optString("phone", "N/A");
                            String address = jsonObject.optString("address", "N/A");
                            String email = jsonObject.optString("email", "N/A");
                            String password = jsonObject.optString("password", "N/A");
                            String imagePath = jsonObject.optString("imagePath", null);

                            runOnUiThread(() -> {
                                displayTextView.setText(fullName);
                                descriptionTextView.setText(email);
                                addressTextView.setText(phone);
                                passwordTextView.setText(password);
                                editGPStext.setText(address);
                                // Load image from imagePath using Glide or Picasso
                                String baseUrl = "https://www.fissadelivery.com/fissa/";
                                String fullImagePath = baseUrl + imagePath.replace("../", "");
                                Glide.with(ProfileActivity.this)
                                        .load(fullImagePath)
                                        .into(profileImageView);

                                Log.d("ProfileActivity", "Final Image URL: " + fullImagePath);

                            });

                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProfileActivity.this, "Error parsing user data", Toast.LENGTH_LONG).show();
                        });
                        Log.e("ProfileActivity", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Error fetching user data", Toast.LENGTH_LONG).show();
                    });
                    Log.e("ProfileActivity", "Response error: " + response.message());
                }
            }
        });
    }
    private void showImageSourceDialog() {
        String[] options = {"Choose from Gallery", "Take a Photo"};
        new AlertDialog.Builder(this)
                .setTitle("Select Profile Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Choose from Gallery
                        if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            openGallery();
                        } else {
                            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
                        }
                    } else if (which == 1) {
                        // Take a Photo
                        if (checkPermission(Manifest.permission.CAMERA)) {
                            openCamera();
                        } else {
                            requestPermission(Manifest.permission.CAMERA, 102);
                        }
                    }
                })
                .show();
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 201);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 202);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 101) {
                openGallery();
            } else if (requestCode == 102) {
                openCamera();
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 201 && data != null) {
                // Handle image from gallery
                Uri selectedImageUri = data.getData();
                profileImageView.setImageURI(selectedImageUri); // Display image
            } else if (requestCode == 202 && data != null) {
                // Handle image from camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImageView.setImageBitmap(photo); // Display image
            }
        }
    }
}