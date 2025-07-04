package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.view.View;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCartItems;
    private CartAdapter cartAdapter;
    private TextView totalAmount, textEmptyCart;
    private Button btnCheckout;
    private ImageView btnBack;
    private List<Product> cartList = new ArrayList<>();


    private List<Product> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Log.d("CartDebug", "CartActivity onCreate triggered");

        // Bind views
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        totalAmount = findViewById(R.id.totalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
        textEmptyCart = findViewById(R.id.textEmptyCart);

        // Handle back press
        btnBack.setOnClickListener(v -> {
            if (isTaskRoot()) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        });

        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this::updateTotal);
        recyclerCartItems.setAdapter(cartAdapter);

        Log.d("CartDebug", "loadCartItems() called");
        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderSummaryActivity.class);
            intent.putParcelableArrayListExtra("orderList", new ArrayList<>(cartItems));
            startActivity(intent);

        });

    }

    private void loadCartItems() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("CartDebug", "loadCartItems() called");
        FirebaseDatabase
                .getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cart")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItems.clear();

                        Log.d("CartDebug", "Items found: " + snapshot.getChildrenCount());

                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            Log.d("CartDebug", "Raw snapshot: " + itemSnapshot.toString());

                            Product product = itemSnapshot.getValue(Product.class);
                            if (product != null) {
                                Log.d("CartDebug", "Item loaded: " + product.title + " | " + product.price);
                                cartItems.add(product);
                            } else {
                                Log.w("CartDebug", "Null Product for key: " + itemSnapshot.getKey());
                            }
                        }

                        cartAdapter.notifyDataSetChanged();
                        updateTotal();

                        if (cartItems.isEmpty()) {
                            textEmptyCart.setVisibility(View.VISIBLE);
                        } else {
                            textEmptyCart.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CartDebug", "Failed to load cart: " + error.getMessage());
                        Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTotal() {
        double total = 0;
        for (Product item : cartItems) {
            total += parsePrice(item.price) * (item.quantity > 0 ? item.quantity : 1);
        }
        totalAmount.setText(String.format("RM %.2f", total));
    }

    private double parsePrice(String priceText) {
        try {
            return Double.parseDouble(priceText.replace("RM", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
