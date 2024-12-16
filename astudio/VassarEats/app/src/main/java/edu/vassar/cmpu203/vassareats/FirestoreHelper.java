package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FirestoreHelper {
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveUserLikedItems(Context context, String userId, List<String> likedItems) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("likedItems", likedItems);

        // Save or update the user's liked items in Firestore
        db.collection("users").document(userId)
                .set(userData, SetOptions.merge()) // Use merge to avoid overwriting other fields
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Data saved successfully for user: " + userId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving data", e));
    }

    public void loadUserLikedItems(String userId, FirestoreCallback callback) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("likedItems")) {
                        List<String> likedItems = (List<String>) documentSnapshot.get("likedItems");
                        callback.onSuccess(likedItems);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading data", e);
                    callback.onFailure(e);
                });
    }

    public void updateLikesCount(String foodId, boolean isLiked) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("likesCount", isLiked ? FieldValue.increment(1) : FieldValue.increment(-1));

        db.collection("foodItems").document(foodId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Likes count updated for food: " + foodId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating likes count", e));
    }


    public void getLikeCount(String foodId, FirestoreCallback2 callback) {
        db.collection("foodItems")
                .document(foodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("likesCount")) {
                        int likesCount = documentSnapshot.getLong("likesCount").intValue();
                        callback.onSuccess(likesCount + " likes");
                    } else {
                        callback.onSuccess("0 likes");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching likes count", e);
                    callback.onFailure(e);
                });
    }



    public interface FirestoreCallback {
        void onSuccess(List<String> likedItems);

        void onFailure(Exception e);
    }

    public interface FirestoreCallback2 {
        void onSuccess(String likedItems);

        void onFailure(Exception e);
    }
}
