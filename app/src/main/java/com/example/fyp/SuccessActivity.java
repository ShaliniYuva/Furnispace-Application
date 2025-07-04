package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        LottieAnimationView animationView = findViewById(R.id.successAnim);
        animationView.setAnimation("success.json");
        animationView.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
