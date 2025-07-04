package com.example.fyp;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextMobile, editTextEmail, editTextPassword;
    private Button btnSave;
    private ImageView btnBack, togglePassword;
    private boolean isPasswordVisible = false;

    private DatabaseReference rootRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        rootRef = FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        editTextName = findViewById(R.id.editTextName);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        togglePassword = findViewById(R.id.togglePassword);

        btnBack.setOnClickListener(v -> finish());

        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = false;
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            editTextPassword.setSelection(editTextPassword.length());
        });

        loadProfile();

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadProfile() {
        rootRef.child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            editTextName.setText(snapshot.child("name").getValue(String.class));
                            editTextMobile.setText(snapshot.child("mobile").getValue(String.class));
                            editTextEmail.setText(snapshot.child("email").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        showStyledToast("Failed to load profile");
                    }
                });
    }

    private void saveChanges() {
        String name = editTextName.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String newPassword = editTextPassword.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty()) {
            showStyledToast("Name, Mobile, and Email cannot be empty");
            return;
        }

        rootRef.child("users").child(uid).child("name").setValue(name);
        rootRef.child("users").child(uid).child("mobile").setValue(mobile);
        rootRef.child("users").child(uid).child("email").setValue(email);

        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                showStyledToast("Password must be at least 6 characters");
                return;
            }

            FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword)
                    .addOnSuccessListener(unused -> showStyledToast("Password updated"))
                    .addOnFailureListener(e -> showStyledToast("Failed to update password: " + e.getMessage()));
        }

        showStyledToast("Profile updated");
        finish();
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
