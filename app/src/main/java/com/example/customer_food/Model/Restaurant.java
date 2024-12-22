package com.example.customer_food.Model;

public class Restaurant {
    private String restaurantId;
    private String restaurantName;
    private String restaurantLocation;
    private String restaurantValue;
    private String restaurantStatus;
    private String restaurantImage; // New field


    // Constructors
    public Restaurant() {}

    public Restaurant(String restaurantId, String restaurantName, String restaurantLocation, String restaurantValue, String restaurantStatus, String restaurantImage) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantLocation = restaurantLocation;
        this.restaurantValue = restaurantValue;
        this.restaurantImage = restaurantImage; // Initialize the new field

    }

    // Getters and Setters
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public String getRestaurantValue() {
        return restaurantValue;
    }

    public void setRestaurantValue(String restaurantValue) {
        this.restaurantValue = restaurantValue;
    }

    public String getRestaurantStatus() {
        return restaurantStatus;
    }

    public void setRestaurantStatus(String restaurantStatus) {
        this.restaurantStatus = restaurantStatus;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    }