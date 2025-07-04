package com.example.fyp;

public class CartItem {
    public String title;
    public String price;
    public String image;
    public String modelUrl;

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String title, String price, String image, String modelUrl) {
        this.title = title;
        this.price = price;
        this.image = image;
        this.modelUrl = modelUrl;
    }
}
