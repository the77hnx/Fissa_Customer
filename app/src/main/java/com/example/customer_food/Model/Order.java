package com.example.customer_food.Model;

public class Order {
    private int orderId;
    private String storeName;
    private double deliveryPrice;
    private double orderPrice;
    private String orderDate;
    private String orderTime;
    private String statusName;
    private String additionalInfomag; // Additional information for the restaurant
    private String additionalInfoliv;  // Additional information for the delivery worker

    public Order(int orderId, String storeName, double deliveryPrice, double orderPrice,
                 String orderDate, String orderTime, String statusName,
                 String additionalInfomag, String additionalInfoliv) {
        this.orderId = orderId;
        this.storeName = storeName;
        this.deliveryPrice = deliveryPrice;
        this.orderPrice = orderPrice;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.statusName = statusName;
        this.additionalInfomag = additionalInfomag;
        this.additionalInfoliv = additionalInfoliv;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public String getStoreName() { return storeName; }
    public double getDeliveryPrice() { return deliveryPrice; }
    public double getOrderPrice() { return orderPrice; }
    public String getOrderDate() { return orderDate; }
    public String getOrderTime() { return orderTime; }
    public String getStatusName() { return statusName; }
    public String getAdditionalInfomag() { return additionalInfomag; }
    public String getAdditionalInfoliv() { return additionalInfoliv; }

    // Setters
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setDeliveryPrice(double deliveryPrice) { this.deliveryPrice = deliveryPrice; }
    public void setOrderPrice(double orderPrice) { this.orderPrice = orderPrice; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public void setAdditionalInfomag(String additionalInfomag) { this.additionalInfomag = additionalInfomag; }
    public void setAdditionalInfoliv(String additionalInfoliv) { this.additionalInfoliv = additionalInfoliv; }
}
