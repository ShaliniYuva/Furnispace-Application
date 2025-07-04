package com.example.fyp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFavouritesManager {

    private static final String TAG = "FirebaseFavourites";

    public static DatabaseReference getRefForUser(String uid) {
        return FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app/")

                .getReference("favourites")
                .child(uid);
    }

    public static void addFavourite(String uid, Product product) {
        getRefForUser(uid).child(product.title).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "✅ Successfully added favourite: " + product.title);
                        localFavCache.put(product.title, true); // update cache too
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "❌ Failed to add favourite: " + e.getMessage());
                    }
                });
    }

    public static void removeFavourite(String uid, Product product) {
        getRefForUser(uid).child(product.title).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "✅ Successfully removed favourite: " + product.title);
                        localFavCache.remove(product.title); // remove from cache
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "❌ Failed to remove favourite: " + e.getMessage());
                    }
                });
    }

    // Local cache to track favourite status
    private static final Map<String, Boolean> localFavCache = new HashMap<>();

    public static void preloadFavourites(String uid, final Runnable onComplete) {
        getRefForUser(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                localFavCache.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    localFavCache.put(child.getKey(), true);
                }
                Log.d(TAG, "✅ Preloaded favourites: " + localFavCache.keySet());
                onComplete.run();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "❌ Failed to preload favourites: " + e.getMessage());
                onComplete.run(); // Still call it to avoid blocking UI
            }
        });
    }

    public static boolean isFavourite(Product product) {
        return localFavCache.containsKey(product.title);
    }
}
