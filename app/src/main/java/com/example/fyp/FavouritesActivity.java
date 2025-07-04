package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerFavourites;
    TextView textEmptyFavourites;
    BottomNavigationView bottomNavigationView;
    ProductAdapter adapter;
    List<Product> favouriteProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerFavourites = findViewById(R.id.recyclerFavourites);
        textEmptyFavourites = findViewById(R.id.textEmptyFavourites);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        adapter = new ProductAdapter(favouriteProducts, this::openProductDetail);
        recyclerFavourites.setLayoutManager(new LinearLayoutManager(this));
        recyclerFavourites.setAdapter(adapter);

        loadFavouritesFromFirebase();

        bottomNavigationView.setSelectedItemId(R.id.nav_fav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));

                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));

                return true;
            } else if (id == R.id.nav_fav) {
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            }  else if (id == R.id.nav_profile) {
                startActivity(new Intent(FavouritesActivity.this, ProfileActivity.class));

                return true;
            }
            return false;
        });

        updateCartCount(); // Show cart badge on this screen too
    }

    private void loadFavouritesFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("favourites")
                .child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteProducts.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    favouriteProducts.add(product);
                }
                adapter.notifyDataSetChanged();

                if (favouriteProducts.isEmpty()) {
                    textEmptyFavourites.setVisibility(View.VISIBLE);
                    recyclerFavourites.setVisibility(View.GONE);
                } else {
                    textEmptyFavourites.setVisibility(View.GONE);
                    recyclerFavourites.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textEmptyFavourites.setText("Failed to load favourites.");
                textEmptyFavourites.setVisibility(View.VISIBLE);
                recyclerFavourites.setVisibility(View.GONE);
            }
        });
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
}
