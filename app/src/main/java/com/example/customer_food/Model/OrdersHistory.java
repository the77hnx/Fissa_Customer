package com.example.customer_food.Model;

import java.util.ArrayList;
import java.util.List;

public class OrdersHistory {
    private static OrdersHistory instance;

    private OrdersHistory() {}

    public static OrdersHistory getInstance() {
        if (instance == null) {
            instance = new OrdersHistory();
        }
        return instance;
    }

    public List<Order> getOrders() {
        // Mock data for testing. Replace with actual database logic.
        List<Order> orders = new ArrayList<>();
        return orders;
    }
}
