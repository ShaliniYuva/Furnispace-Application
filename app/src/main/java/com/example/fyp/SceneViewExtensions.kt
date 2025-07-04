package com.example.fyp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import io.github.sceneview.SceneView
import androidx.core.graphics.createBitmap

fun SceneView.capture(onCaptured: (Bitmap) -> Unit) {
    // Ensure the SceneView is a View instance
    val view = this as? View
    view?.post {
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        onCaptured(bitmap)
    }
}
