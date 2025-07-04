package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextMobile, editTextEmail, editTextPassword;
    Button buttonSignUp;
    TextView textLoginRedirect;
    ImageView imageViewToggle;

    boolean isPasswordVisible = false;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textLoginRedirect = findViewById(R.id.textLoginRedirect);
        imageViewToggle = findViewById(R.id.imageViewToggle);

        mAuth = FirebaseAuth.getInstance();

        // Toggle password visibility
        imageViewToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageViewToggle.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = false;
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageViewToggle.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            editTextPassword.setSelection(editTextPassword.length());
        });

        // Phone number auto formatting
        editTextMobile.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private boolean isFormatting;
            private int cursorPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isFormatting) {
                    current = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isFormatting) {
                    cursorPosition = editTextMobile.getSelectionStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String digits = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length() && i < 11; i++) {
                    formatted.append(digits.charAt(i));
                    if (i == 2 || i == 5) {
                        formatted.append(" ");
                    }
                }

                String newText = formatted.toString();
                editTextMobile.setText(newText);

                int newCursorPosition = cursorPosition;
                if (cursorPosition == 4 || cursorPosition == 8) {
                    newCursorPosition++;
                } else if (current.length() > s.length() && (cursorPosition == 4 || cursorPosition == 8)) {
                    newCursorPosition--;
                }

                if (newCursorPosition > newText.length()) newCursorPosition = newText.length();
                if (newCursorPosition < 0) newCursorPosition = 0;

                editTextMobile.setSelection(newCursorPosition);
                isFormatting = false;
            }
        });

        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String mobile = editTextMobile.getText().toString().replaceAll("\\s+", "").trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (name.isEmpty()) {
                editTextName.setError("Name is required");
                return;
            }

            if (!isValidPhone(mobile)) {
                editTextMobile.setError("Enter a valid Malaysian phone number (e.g. 0123456789)");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.endsWith("@gmail.co")) {
                editTextEmail.setError("Enter a valid email (e.g., user@gmail.com)");
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            DatabaseReference ref = FirebaseDatabase
                                    .getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("users").child(uid);

                            ref.child("name").setValue(name);
                            ref.child("mobile").setValue(mobile);
                            ref.child("email").setValue(email);

                            Intent intent = new Intent(RegisterActivity.this, SuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            editTextEmail.setError("Registration failed: " + task.getException().getMessage());
                        }
                    });
        });

        textLoginRedirect.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, Login.class)));
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^01\\d{8,9}$");
    }
}
