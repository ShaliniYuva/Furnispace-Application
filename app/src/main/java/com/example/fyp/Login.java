package com.example.fyp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class Login extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView textForgotPassword, textCreateAccount;
    ImageView imageViewToggle;

    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        textCreateAccount = findViewById(R.id.textCreateAccount);
        imageViewToggle = findViewById(R.id.imageViewToggle);

        mAuth = FirebaseAuth.getInstance();

        // 👁 Toggle password visibility
        imageViewToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // 🔐 Login logic
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if (!isValidEmail(email)) {
                editTextEmail.setError("Please enter a valid email");
                editTextEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                editTextPassword.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginTask -> {
                        if (loginTask.isSuccessful()) {
                            startActivity(new Intent(Login.this, SuccessActivity.class));
                            finish();
                        } else {
                            Exception e = loginTask.getException();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                editTextPassword.setError("Incorrect email or password");
                                editTextPassword.requestFocus();
                            } else if (e instanceof FirebaseAuthException) {
                                Toast.makeText(Login.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseException) {
                                Toast.makeText(Login.this, "Network error: Please try again later", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        textCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, RegisterActivity.class));
        });

        textForgotPassword.setOnClickListener(v -> {
            EditText resetMail = new EditText(this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Reset Password");
            dialog.setMessage("Enter your email to receive reset link:");
            dialog.setView(resetMail);

            dialog.setPositiveButton("Send", (d, which) -> {
                String email = resetMail.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Login.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(Login.this, "Reset link sent to your email.", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            });

            dialog.setNegativeButton("Cancel", (d, which) -> {
                // dialog closed
            });

            dialog.create().show();
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
