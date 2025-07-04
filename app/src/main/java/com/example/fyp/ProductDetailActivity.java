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

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView productImage, btnBack, btnFavorite, btnOrderWhatsapp;
    TextView productTitle, productPrice, productDescription, productDimensions;
    Button btnViewAR, btnAddToCart;
    BottomNavigationView bottomNavigationView;

    String modelUrl = "";
    String productName = "";
    String productPriceText = "";
    String imageName = "";
    Product currentProduct;

    String databaseUrl = "https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productDimensions = findViewById(R.id.productDimensions);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnOrderWhatsapp = findViewById(R.id.btnOrderWhatsapp);
        btnViewAR = findViewById(R.id.btnViewAR);
        btnAddToCart = findViewById(R.id.btnAddCart);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Intent intent = getIntent();
        imageName = intent.getStringExtra("img");
        productName = intent.getStringExtra("title");
        productPriceText = intent.getStringExtra("price");
        String description = intent.getStringExtra("desc");
        String dimensions = intent.getStringExtra("dim");
        modelUrl = intent.getStringExtra("modelUrl");

        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (imageResId != 0) {
            productImage.setImageResource(imageResId);
        } else {
            productImage.setImageResource(R.drawable.placeholder);
        }

        productTitle.setText(productName);
        productPrice.setText(productPriceText);
        productDescription.setText(description);
        productDimensions.setText(dimensions);

        currentProduct = new Product(productName, productPriceText, description, dimensions, imageName, modelUrl);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFavouritesManager.preloadFavourites(uid, this::updateFavouriteIcon);

        updateCartCount();

        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            if (FirebaseFavouritesManager.isFavourite(currentProduct)) {
                FirebaseFavouritesManager.removeFavourite(uid, currentProduct);
                btnFavorite.setImageResource(R.drawable.icon_favourites);
                showStyledToast("Removed from favourites");
            } else {
                FirebaseFavouritesManager.addFavourite(uid, currentProduct);
                btnFavorite.setImageResource(R.drawable.icon_favourites_filled);
                showStyledToast("Added to favourites");
            }
        });

        btnViewAR.setOnClickListener(v -> {
            if (modelUrl == null || modelUrl.isEmpty()) {
                showStyledToast("AR model not available");
                return;
            }
            Intent arIntent = new Intent(this, ArViewActivity.class);
            arIntent.putExtra("modelUrl", modelUrl);
            arIntent.putExtra("title", productName);
            arIntent.putExtra("price", productPriceText);
            startActivity(arIntent);
        });

        btnOrderWhatsapp.setOnClickListener(v -> sendEnquiryToWhatsApp());
        btnAddToCart.setOnClickListener(v -> addToCart(currentProduct));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(this, FavouritesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            }
            return false;
        });
    }

    private void addToCart(Product product) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("cart")
                .child(uid)
                .child(product.title);

        cartRef.setValue(product).addOnSuccessListener(unused -> {
            showStyledToast("Added to cart");
            updateCartCount();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCartCount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("cart")
                .child(uid);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                setCartBadge(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setCartBadge(int count) {
        bottomNavigationView.post(() -> {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
            badge.setNumber(count);
            badge.setVisible(count > 0);
        });
    }

    private void sendEnquiryToWhatsApp() {
        String message = "Hi, I’d like to know more about this product:\n\n"
                + productName + "\nPrice: " + productPriceText;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://wa.me/60123501041?text=" + Uri.encode(message)));


        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavouriteIcon() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favRef = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("favourites")
                .child(uid)
                .child(currentProduct.title);

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnFavorite.setImageResource(R.drawable.icon_favourites_filled);
                } else {
                    btnFavorite.setImageResource(R.drawable.icon_favourites);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnFavorite.setImageResource(R.drawable.icon_favourites);
            }
        });
    }

    private void showStyledToast(String message) {
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.textToastMessage);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
