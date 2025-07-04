package com.example.fyp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView recyclerOrderItems;
    private TextView textTotalAmount;
    private Button btnPlaceOrder;
    private OrderSummaryAdapter adapter;
    private List<Product> cartItems = new ArrayList<>();
    private DatabaseReference rootRef;
    private String uid;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        btnBack = findViewById(R.id.btnBack);
        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);
        textTotalAmount = findViewById(R.id.textTotalAmount);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        cartItems = getIntent().getParcelableArrayListExtra("orderList");

        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderSummaryAdapter(cartItems);
        recyclerOrderItems.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnPlaceOrder.setOnClickListener(v -> placeOrderViaWhatsApp());

        calculateTotal();
    }
    private void calculateTotal() {
        double total = 0;
        for (Product product : cartItems) {
            int quantity = product.quantity > 0 ? product.quantity : 1;
            double price = parsePrice(product.price);
            total += price * quantity;
        }
        textTotalAmount.setText(String.format("RM %.2f", total));
    }


    private void loadCartItems() {
        List<Product> receivedList = getIntent().getParcelableArrayListExtra("orderList");

        if (receivedList != null && !receivedList.isEmpty()) {
            cartItems.clear();
            cartItems.addAll(receivedList);

            double total = 0;
            for (Product product : cartItems) {
                int quantity = product.quantity > 0 ? product.quantity : 1;
                double price = parsePrice(product.price);
                total += price * quantity;
            }

            adapter.notifyDataSetChanged();
            textTotalAmount.setText(String.format("RM %.2f", total));
        } else {
            Toast.makeText(this, "No items received", Toast.LENGTH_SHORT).show();
        }
    }


    private void placeOrderViaWhatsApp() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Hi, I would like to place an order for the following items:\n");

        double total = 0;
        for (Product product : cartItems) {
            int quantity = product.quantity > 0 ? product.quantity : 1;
            message.append("- x").append(quantity).append(" ").append(product.title)
                    .append(" (").append(product.price).append(")\n");

            double price = parsePrice(product.price);
            total += price * quantity;
        }

        message.append("Total: RM ").append(String.format("%.2f", total)).append("\nThank you.");

        String url = "https://wa.me/60123501041?text=" + Uri.encode(message.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private double parsePrice(String priceText) {
        try {
            return Double.parseDouble(priceText.replace("RM", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
