package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText searchInput;
    RecyclerView searchResultsRecycler;
    TextView textNoResults;
    BottomNavigationView bottomNavigationView;

    List<Product> productList;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        searchResultsRecycler = findViewById(R.id.searchResultsRecycler);
        textNoResults = findViewById(R.id.textNoResults);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        productList = getAllProducts();

        adapter = new ProductAdapter(productList, this::openProductDetail);
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecycler.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_search); // highlight search tab

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SearchActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);

                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(SearchActivity.this, FavouritesActivity.class));
                overridePendingTransition(0, 0);

                return true;
            } else if (id == R.id.nav_cart) {
                Intent intent = new Intent(SearchActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));

                return true;
            }
            return false;
        });

        updateCartCount(); // Show cart badge on this screen too
    }

    private void filterProducts(String query) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : productList) {
            if (p.title.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
        textNoResults.setVisibility(filtered.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    private void openProductDetail(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("title", product.title);
        intent.putExtra("price", product.price);
        intent.putExtra("desc", product.description);
        intent.putExtra("dim", product.dimensions);
        intent.putExtra("img", product.imageName);

        intent.putExtra("modelUrl", product.modelUrl);
        startActivity(intent);
    }

    private List<Product> getAllProducts() {
        List<Product> items = new ArrayList<>();

        items.add(new Product("Scott Armchair", "RM 500", "Soft and comfy armchair, perfect for relaxing.",
                "Width: 75cm\nHeight: 85cm\nDepth: 80cm","armchair2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Farmchair.glb?alt=media&token=e02c7939-33c9-44c2-83cc-da6b8f691b8c"));

        items.add(new Product("Lounge Chair", "RM 550", "A modern lounge chair with ergonomic comfort.",
                "Width: 80cm\nHeight: 70cm\nDepth: 78cm", "lounge_chair2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Flounge_chair.glb?alt=media&token=e7bfcca9-9558-4050-ae66-584652d88098"));

        items.add(new Product("Study Table", "RM 150", "Compact study table for focused work sessions.",
                "Width: 100cm\nHeight: 75cm\nDepth: 50cm", "study_table2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fstudy_table.glb?alt=media&token=b4b885b9-f60e-4a59-a885-7ed148e91350"));

        items.add(new Product("Dining Table", "RM 1200", "Elegant dining table set perfect for family meals.",
                "Width: 120cm\nHeight: 75cm\nDepth: 70cm", "dining_table_set2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fdining_table_set.glb?alt=media&token=a0b920db-92b6-4d43-b60f-c57fa5f294f3"));

        items.add(new Product("Coffee Table", "RM 300", "Minimalist coffee table to enhance your living room.",
                "Width: 90cm\nHeight: 40cm\nDepth: 60cm", "coffee_table2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fpeyton_gm_coffee_table.glb?alt=media&token=276f6e36-2edd-4fd8-9135-83b7b8c02ff5"));

        items.add(new Product("Queen Bed", "RM 2000", "Spacious queen-size bed for restful sleep.",
                "Width: 160cm\nHeight: 110cm\nDepth: 200cm", "bed2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fbed.glb?alt=media&token=04b61542-78b1-4a90-9eb9-ce21d9b0d739"));

        return items;
    }

    private void updateCartCount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase
                .getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cart")
                .child(uid);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                setCartBadge(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setCartBadge(int count) {
        bottomNavigationView.post(() -> {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
            badge.setNumber(count);
            badge.setVisible(count > 0);
        });
    }
}
