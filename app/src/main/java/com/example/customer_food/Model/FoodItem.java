package com.example.customer_food.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {
    private String name;
    private double price;
    private int count;
    private String imageUrl; // Add image URL attribute


    public FoodItem(String name, double price, int count, String imageUrl) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.imageUrl = imageUrl;

    }

    protected FoodItem(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        count = in.readInt();
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    public void incrementCount() {
        this.count++;
    }

    public void decrementCount() {
        if (this.count > 0) {
            this.count--;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(count);
    }
}