package com.example.fyp;

import android.content.Intent;
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

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack, profileImage;
    private TextView textName, textPhone, textEmail, textEditProfile;
    private Button btnLogout;
    private BottomNavigationView bottomNavigationView;

    private DatabaseReference rootRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rootRef = FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnBack = findViewById(R.id.btnBack);
        profileImage = findViewById(R.id.profileImage);
        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textEmail = findViewById(R.id.textEmail);
        textEditProfile = findViewById(R.id.textEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        profileImage.setImageResource(R.drawable.ic_user);

        btnBack.setOnClickListener(v -> finish());

        textEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));

                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));

                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(this, FavouritesActivity.class));

                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));

                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        loadProfile();
        updateCartCount();
    }

    private void loadProfile() {
        rootRef.child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("name").getValue(String.class);
                            String mobile = snapshot.child("mobile").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);

                            textName.setText(name != null ? name : "-");
                            textPhone.setText(mobile != null ? mobile : "-");
                            textEmail.setText(email != null ? email : "-");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCartCount() {
        rootRef.child("cart").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount();
                        setCartBadge(count);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
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
