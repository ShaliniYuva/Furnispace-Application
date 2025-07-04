package com.example.fyp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ArViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get data from intent
        val modelUrl = intent.getStringExtra("modelUrl")
        val productTitle = intent.getStringExtra("title") ?: "Furniture"
        val productPrice = intent.getStringExtra("price") ?: ""

        if (modelUrl.isNullOrEmpty()) {
            Toast.makeText(this, "AR model not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Launch Scene Viewer intent with model URL and details
        val sceneViewerIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                .buildUpon()
                .appendQueryParameter("file", modelUrl)
                .appendQueryParameter("mode", "ar_only")
                .appendQueryParameter("resizable", "false")
                .appendQueryParameter("title", "$productTitle - $productPrice")
                .build()
            setPackage("com.google.ar.core")
        }

        try {
            startActivity(sceneViewerIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Google ARCore not available", Toast.LENGTH_LONG).show()
        }

        // Optional: finish activity if it's only for launching AR
        finish()
    }
}
