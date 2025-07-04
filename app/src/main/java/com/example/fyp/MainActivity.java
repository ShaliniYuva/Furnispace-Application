package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(v -> {
            // Replace with actual next screen
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        });
    }
}
