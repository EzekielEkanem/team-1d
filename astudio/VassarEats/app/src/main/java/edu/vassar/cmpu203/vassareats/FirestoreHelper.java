package edu.vassar.cmpu203.vassareats;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.google.genai.Client;
import com.google.common.collect.ImmutableList;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.genai.ResponseStream;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ImageConfig;
import com.google.genai.types.Part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;

public class FirestoreHelper {
    private final FirebaseFirestore db;
    private static final java.util.concurrent.Semaphore IMAGE_GEN_SEMAPHORE =
            new java.util.concurrent.Semaphore(2); // Limit to 2 concurrent requests

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

    public interface FirestoreImageCallback {
        void onSuccess(byte[] imageBytes);
        void onFailure(Exception e);
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

    public void loadImageForFood(String foodId, FirestoreImageCallback callback) {
        if (foodId == null) {
            callback.onSuccess(null);
            return;
        }

        db.collection("food_images")
                .document(foodId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onSuccess(null);
                        return;
                    }

                    String base64Image = doc.getString("imageBase64");
                    if (base64Image == null || base64Image.isEmpty()) {
                        callback.onSuccess(null);
                        return;
                    }

                    try {
                        byte[] imageBytes = Base64.decode(base64Image, Base64.NO_WRAP);
                        callback.onSuccess(imageBytes);
                    } catch (IllegalArgumentException e) {
                        callback.onFailure(e);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }


    public void saveImageForFood(String foodId, byte[] imageBytes) {
        if (foodId == null || imageBytes == null) return;

        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        Map<String, Object> data = new HashMap<>();
        data.put("imageBase64", base64Image);

        db.collection("food_images")
                .document(foodId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("FirestoreHelper", "Saved image bytes for " + foodId))
                .addOnFailureListener(e ->
                        Log.e("FirestoreHelper", "Failed saving image bytes for " + foodId, e));
    }

    public void generateNanobananaImage(String prompt, FirestoreImageCallback callback) {
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            boolean acquired = false;
            try {
                // BLOCK until allowed (prevents 429)
                IMAGE_GEN_SEMAPHORE.acquire();
                acquired = true;

                String apiKey = BuildConfig.NANOBANANA_API_KEY;
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    mainThreadHandler.post(() ->
                            callback.onFailure(new Exception("Missing NANOBANANA_API_KEY")));
                    return;
                }

                Client client = Client.builder()
                        .apiKey(apiKey)
                        .build();

                List<Content> contents = ImmutableList.of(
                        Content.builder()
                                .role("user")
                                .parts(ImmutableList.of(Part.fromText(prompt)))
                                .build()
                );

                GenerateContentConfig config =
                        GenerateContentConfig.builder()
                                .responseModalities(ImmutableList.of("IMAGE"))
                                .imageConfig(ImageConfig.builder().build())
                                .build();

                ResponseStream<GenerateContentResponse> stream =
                        client.models.generateContentStream(
                                "gemini-3-pro-image-preview",
                                contents,
                                config
                        );

                for (GenerateContentResponse res : stream) {
                    if (res.candidates().isEmpty()) continue;

                    Content content = res.candidates().get().get(0).content().orElse(null);
                    if (content == null || content.parts().isEmpty()) continue;

                    for (Part part : content.parts().get()) {
                        if (part.inlineData().isPresent()) {
                            Blob blob = part.inlineData().get();
                            byte[] imageBytes = blob.data().orElse(null);
                            if (imageBytes == null) continue;

                            mainThreadHandler.post(() -> callback.onSuccess(imageBytes));
                            stream.close();
                            return;
                        }
                    }
                }

                stream.close();
                mainThreadHandler.post(() ->
                        callback.onFailure(new Exception("No image data returned")));

            } catch (Exception e) {
                Log.e("Nanobanana", "generateNanobananaImage failed", e);
                mainThreadHandler.post(() -> callback.onFailure(e));
            } finally {
                if (acquired) {
                    IMAGE_GEN_SEMAPHORE.release();
                }
            }
        }).start();
    }
}
