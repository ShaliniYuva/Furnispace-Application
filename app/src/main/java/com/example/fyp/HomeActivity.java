package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import android.widget.ImageView;


public class HomeActivity extends AppCompatActivity {

    TextView textAppName, textWelcomeUser;
    MaterialButton btnChair, btnBed, btnTable, btnCupboard;
    BottomNavigationView bottomNavigationView;
    TextView textNoItems;
    ImageView imageUserProfile;

    View itemArmchair, itemLoungeChair, itemDiningTable, itemStudyTable, itemCoffeeTable, itemBed;

    private String currentFilter = "";

    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);
        rootRef = FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();



        textAppName = findViewById(R.id.textAppName);
        textWelcomeUser = findViewById(R.id.textWelcomeUser);
        btnChair = findViewById(R.id.btnChair);
        btnBed = findViewById(R.id.btnBed);
        btnTable = findViewById(R.id.btnTable);
        btnCupboard = findViewById(R.id.btnCupboard);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textNoItems = findViewById(R.id.textNoItems);
        imageUserProfile = findViewById(R.id.imageUserProfile);

        itemArmchair = findViewById(R.id.cardArmchair);
        itemLoungeChair = findViewById(R.id.cardLoungeChair);
        itemDiningTable = findViewById(R.id.cardDiningTable);
        itemStudyTable = findViewById(R.id.cardStudyTable);
        itemCoffeeTable = findViewById(R.id.cardCoffeeTable);
        itemBed = findViewById(R.id.cardBed);

        // Set product card click listeners
        itemArmchair.setOnClickListener(this::openProduct);
        itemLoungeChair.setOnClickListener(this::openProduct);
        itemDiningTable.setOnClickListener(this::openProduct);
        itemStudyTable.setOnClickListener(this::openProduct);
        itemCoffeeTable.setOnClickListener(this::openProduct);
        itemBed.setOnClickListener(this::openProduct);

        showAllItems();
        uncheckAllButtons();

        btnChair.setOnClickListener(v -> handleCategoryToggle(btnChair, "Chair"));
        btnBed.setOnClickListener(v -> handleCategoryToggle(btnBed, "Bed"));
        btnTable.setOnClickListener(v -> handleCategoryToggle(btnTable, "Table"));
        btnCupboard.setOnClickListener(v -> handleCategoryToggle(btnCupboard, "Cupboard"));

        imageUserProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);

                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(HomeActivity.this, FavouritesActivity.class));
                overridePendingTransition(0, 0);

                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                overridePendingTransition(0, 0);

                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));

                return true;
            }
            return false;
        });

        bottomNavigationView.post(this::updateCartCount);

        // Add to cart buttons
        Product armchairProduct = new Product(
                "Scott Armchair",
                "RM 500",
                "Soft and comfy armchair, perfect for relaxing.",
                "Width: 75cm\nHeight: 85cm\nDepth: 80cm",
                "armchair2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Farmchair.glb?alt=media&token=e02c7939-33c9-44c2-83cc-da6b8f691b8c"
        );
        itemArmchair.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(armchairProduct));

        Product loungeChairProduct = new Product(
                "Lounge Chair",
                "RM 550",
                "A modern lounge chair with ergonomic comfort.",
                "Width: 80cm\nHeight: 70cm\nDepth: 78cm",
                "lounge_chair2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Flounge_chair.glb?alt=media&token=e7bfcca9-9558-4050-ae66-584652d88098"
        );
        itemLoungeChair.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(loungeChairProduct));

        Product diningTableProduct = new Product(
                "Dining Table Set",
                "RM 1200",
                "Elegant dining table set perfect for family meals.",
                "Width: 120cm\nHeight: 75cm\nDepth: 70cm",
                "dining_table_set2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fdining_table_set.glb?alt=media&token=a0b920db-92b6-4d43-b60f-c57fa5f294f3"
        );
        itemDiningTable.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(diningTableProduct));

        Product studyTableProduct = new Product(
                "Study Table",
                "RM 150",
                "Compact study table for focused work sessions.",
                "Width: 100cm\nHeight: 75cm\nDepth: 50cm",
                "study_table2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fstudy_table.glb?alt=media&token=b4b885b9-f60e-4a59-a885-7ed148e91350"
        );
        itemStudyTable.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(studyTableProduct));

        Product coffeeTableProduct = new Product(
                "Coffee Table",
                "RM 300",
                "Minimalist coffee table to enhance your living room.",
                "Width: 90cm\nHeight: 40cm\nDepth: 60cm",
                "coffee_table2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fpeyton_gm_coffee_table.glb?alt=media&token=276f6e36-2edd-4fd8-9135-83b7b8c02ff5"
        );
        itemCoffeeTable.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(coffeeTableProduct));

        Product bedProduct = new Product(
                "Queen Bed",
                "RM 2000",
                "Spacious queen-size bed for restful sleep.",
                "Width: 160cm\nHeight: 110cm\nDepth: 200cm",
                "bed2",
                "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fbed.glb?alt=media&token=04b61542-78b1-4a90-9eb9-ce21d9b0d739"
        );
        itemBed.findViewById(R.id.button_add).setOnClickListener(v -> addToCart(bedProduct));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    private void updateCartCount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef.child("cart").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount();
                        setCartBadge(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void setCartBadge(int count) {
        bottomNavigationView.post(() -> {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
            badge.setNumber(count);
            badge.setVisible(count > 0);
        });
    }

    private void handleCategoryToggle(MaterialButton selectedButton, String category) {
        if (currentFilter.equals(category)) {
            currentFilter = "";
            showAllItems();
            uncheckAllButtons();
        } else {
            currentFilter = category;
            filterByCategory(category);
            checkOnlySelected(selectedButton);
        }
    }

    private void checkOnlySelected(MaterialButton selected) {
        MaterialButton[] allButtons = {btnChair, btnBed, btnTable, btnCupboard};
        for (MaterialButton button : allButtons) {
            button.setChecked(button == selected);
        }
    }

    private void uncheckAllButtons() {
        btnChair.setChecked(false);
        btnBed.setChecked(false);
        btnTable.setChecked(false);
        btnCupboard.setChecked(false);
    }

    private void showAllItems() {
        itemArmchair.setVisibility(View.VISIBLE);
        itemLoungeChair.setVisibility(View.VISIBLE);
        itemDiningTable.setVisibility(View.VISIBLE);
        itemStudyTable.setVisibility(View.VISIBLE);
        itemCoffeeTable.setVisibility(View.VISIBLE);
        itemBed.setVisibility(View.VISIBLE);
        textNoItems.setVisibility(View.GONE);
    }

    private void filterByCategory(String category) {
        itemArmchair.setVisibility(View.GONE);
        itemLoungeChair.setVisibility(View.GONE);
        itemDiningTable.setVisibility(View.GONE);
        itemStudyTable.setVisibility(View.GONE);
        itemCoffeeTable.setVisibility(View.GONE);
        itemBed.setVisibility(View.GONE);
        textNoItems.setVisibility(View.GONE);

        boolean hasItems = false;

        if (category.equals("Chair")) {
            itemArmchair.setVisibility(View.VISIBLE);
            itemLoungeChair.setVisibility(View.VISIBLE);
            hasItems = true;
        } else if (category.equals("Table")) {
            itemDiningTable.setVisibility(View.VISIBLE);
            itemStudyTable.setVisibility(View.VISIBLE);
            itemCoffeeTable.setVisibility(View.VISIBLE);
            hasItems = true;
        } else if (category.equals("Bed")) {
            itemBed.setVisibility(View.VISIBLE);
            hasItems = true;
        }

        if (!hasItems) {
            textNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.textToastMessage);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    private void addToCart(Product product) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = rootRef.child("cart").child(uid).child(product.title);

        cartRef.setValue(product).addOnSuccessListener(aVoid -> {
            showToast("Added to cart");
            updateCartCount();
        }).addOnFailureListener(e -> {
            showToast("Failed to add to cart");
        });
    }

    public void openProduct(View view) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        int viewId = view.getId();

        if (viewId == R.id.cardArmchair) {
            intent.putExtra("title", "Scott Armchair");
            intent.putExtra("price", "RM 500");
            intent.putExtra("desc", "Soft and comfy armchair, perfect for relaxing.");
            intent.putExtra("dim", "Width: 75cm\nHeight: 85cm\nDepth: 80cm");
            intent.putExtra("img", "armchair2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Farmchair.glb?alt=media&token=e02c7939-33c9-44c2-83cc-da6b8f691b8c");

        } else if (viewId == R.id.cardLoungeChair) {
            intent.putExtra("title", "Lounge Chair");
            intent.putExtra("price", "RM 550");
            intent.putExtra("desc", "A modern lounge chair with ergonomic comfort.");
            intent.putExtra("dim", "Width: 80cm\nHeight: 70cm\nDepth: 78cm");
            intent.putExtra("img", "lounge_chair2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Flounge_chair.glb?alt=media&token=e7bfcca9-9558-4050-ae66-584652d88098");

        } else if (viewId == R.id.cardDiningTable) {
            intent.putExtra("title", "Dining Table Set");
            intent.putExtra("price", "RM 1200");
            intent.putExtra("desc", "Elegant dining table set perfect for family meals.");
            intent.putExtra("dim", "Width: 120cm\nHeight: 75cm\nDepth: 70cm");
            intent.putExtra("img", "dining_table_set2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fdining_table_set.glb?alt=media&token=a0b920db-92b6-4d43-b60f-c57fa5f294f3");

        } else if (viewId == R.id.cardStudyTable) {
            intent.putExtra("title", "Study Table");
            intent.putExtra("price", "RM 150");
            intent.putExtra("desc", "Compact study table for focused work sessions.");
            intent.putExtra("dim", "Width: 100cm\nHeight: 75cm\nDepth: 50cm");
            intent.putExtra("img", "study_table2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fstudy_table.glb?alt=media&token=b4b885b9-f60e-4a59-a885-7ed148e91350");

        } else if (viewId == R.id.cardCoffeeTable) {
            intent.putExtra("title", "Coffee Table");
            intent.putExtra("price", "RM 300");
            intent.putExtra("desc", "Minimalist coffee table to enhance your living room.");
            intent.putExtra("dim", "Width: 90cm\nHeight: 40cm\nDepth: 60cm");
            intent.putExtra("img", "coffee_table2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fpeyton_gm_coffee_table.glb?alt=media&token=276f6e36-2edd-4fd8-9135-83b7b8c02ff5");

        } else if (viewId == R.id.cardBed) {
            intent.putExtra("title", "Queen Bed");
            intent.putExtra("price", "RM 2000");
            intent.putExtra("desc", "Spacious queen-size bed for restful sleep.");
            intent.putExtra("dim", "Width: 160cm\nHeight: 110cm\nDepth: 200cm");
            intent.putExtra("img", "bed2");
            intent.putExtra("modelUrl", "https://firebasestorage.googleapis.com/v0/b/arapps-final.firebasestorage.app/o/models%2Fbed.glb?alt=media&token=04b61542-78b1-4a90-9eb9-ce21d9b0d739");
        }

        startActivity(intent);
    }
}
