package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
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

    public void updateLikesCount(String foodId, long change, final CompletionCallback callback) {
        DocumentReference foodDocRef = db.collection("foods").document(foodId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(foodDocRef);
            long newLikes = 0;
            if (snapshot.exists() && snapshot.getLong("likes") != null) {
                newLikes = snapshot.getLong("likes") + change;
            } else {
                // If the document or field doesn't exist, start the count
                newLikes = (change > 0) ? 1 : 0;
            }
            // Ensure likes don't go below zero
            if (newLikes < 0) {
                newLikes = 0;
            }
            transaction.update(foodDocRef, "likes", newLikes);
            return null; // A transaction must return something, null is fine here
        }).addOnSuccessListener(aVoid -> {
            Log.d("FirestoreHelper", "Likes count transaction successful for " + foodId);
            callback.onComplete(true, null); // Call onComplete with success
        }).addOnFailureListener(e -> {
            Log.e("FirestoreHelper", "Likes count transaction failed for " + foodId, e);
            callback.onComplete(false, e); // Call onComplete with failure
        });
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

    /**
     * A simple callback for Firestore operations that do not return data.
     */
    public interface CompletionCallback {
        void onComplete(boolean success, Exception e);
    }

    public void loadUserDislikedItems(String userId, final FirestoreCallback callback) {
        if (userId == null) {
            callback.onSuccess(new ArrayList<>());
            return;
        }
        db.collection("users").document(userId).get()
                .addOnSuccessListener((DocumentSnapshot documentSnapshot) -> {
                    List<String> disliked = documentSnapshot.get("dislikedItems") instanceof List
                            ? (List<String>) documentSnapshot.get("dislikedItems")
                            : new ArrayList<>();
                    callback.onSuccess(disliked);
                })
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void saveUserDislikedItems(Context context, String userId, List<String> dislikedItems) {
        if (userId == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("dislikedItems", dislikedItems != null ? dislikedItems : new ArrayList<>());
        db.collection("users").document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("FirestoreHelper", "Saved disliked items"))
                .addOnFailureListener(e -> Log.e("FirestoreHelper", "Failed saving disliked items", e));
    }
}
