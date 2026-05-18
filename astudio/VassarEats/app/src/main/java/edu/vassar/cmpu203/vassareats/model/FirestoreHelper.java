package edu.vassar.cmpu203.vassareats.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.google.genai.Client;
import com.google.common.collect.ImmutableList;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.concurrent.Semaphore;

import edu.vassar.cmpu203.vassareats.BuildConfig;
import edu.vassar.cmpu203.vassareats.view.ExpandableRecyclerViewAdapter;

public class FirestoreHelper {
    public final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private static final Semaphore IMAGE_GEN_SEMAPHORE =
            new Semaphore(10); // Limit to 2 concurrent requests

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void saveUserLikedItems(String userId, List<String> likedItems) {
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

    public interface FirestoreNutritionCallback {
        void onSuccess(Map<String, String> nutritionDetails);
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

    public void saveUserReportedItems(String userId, List<String> reportedItems) {
        if (userId == null) return;
        Map<String, Object> userData = new HashMap<>();
        userData.put("reportedItems", reportedItems != null ? reportedItems : new ArrayList<>());

        // Save or update the user's reported items in Firestore
        db.collection("users").document(userId)
                .set(userData, SetOptions.merge()) // Use merge to avoid overwriting other fields
                .addOnSuccessListener(aVoid -> Log.d("FirestoreHelper", "Reported items saved successfully for user: " + userId))
                .addOnFailureListener(e -> Log.e("FirestoreHelper", "Error saving reported items", e));
    }

    public void loadUserReportedItems(String userId, FirestoreCallback callback) {
        if (userId == null) {
            callback.onSuccess(new ArrayList<>());
            return;
        }
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("reportedItems")) {
                        List<String> reportedItems = (List<String>) documentSnapshot.get("reportedItems");
                        callback.onSuccess(reportedItems);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error loading reported items", e);
                    callback.onFailure(e);
                });
    }

    public void loadImageForFood(String foodId, Context context, FirestoreImageCallback callback) {
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

                    String imageUrl = doc.getString("imageUrl");
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        callback.onSuccess(null);
                        return;
                    }

                    // Use ImageCacheManager for cached loading
                    ImageCacheManager cacheManager = new ImageCacheManager(
                            context, // or pass context
                            400, // target width
                            300  // target height
                    );

                    cacheManager.loadImageWithCache(imageUrl, new ImageCacheManager.ImageLoadCallback() {
                        @Override
                        public void onSuccess(byte[] imageBytes) {
                            callback.onSuccess(imageBytes);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FirestoreHelper", "Cache load failed, falling back", e);
                            callback.onFailure(e);
                        }
                    });
                })
                .addOnFailureListener(callback::onFailure);
    }


    public void saveImageForFood(String foodId, byte[] imageBytes) {
        if (foodId == null || imageBytes == null) return;

        // Create a reference to Firebase Storage
        StorageReference imageRef = storage.getReference()
                .child("food_images")
                .child(foodId + ".jpg");

        // Upload the image
        UploadTask uploadTask = imageRef.putBytes(imageBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Store the URL in Firestore
                Map<String, Object> data = new HashMap<>();
                data.put("imageUrl", imageUrl);

                db.collection("food_images")
                        .document(foodId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid ->
                                Log.d("FirestoreHelper", "Saved image URL for " + foodId))
                        .addOnFailureListener(e ->
                                Log.e("FirestoreHelper", "Failed saving image URL for " + foodId, e));
            });
        }).addOnFailureListener(e ->
                Log.e("FirestoreHelper", "Failed uploading image for " + foodId, e));
    }

    public void migrateImageToStorage(String foodId) {
        if (foodId == null) {
            Log.e("Migration", "foodId is null");
            return;
        }
        db.collection("food_images").document(foodId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Log.w("Migration", "Document doesn't exist for: " + foodId);
                        return;
                    }
                    String base64Image = doc.getString("imageBase64");
                    if (base64Image == null || base64Image.isEmpty()) {
                        Log.w("Migration", "No imageBase64 field for: " + foodId);
                        return;
                    }
                    try {
                        byte[] imageBytes = Base64.decode(base64Image, Base64.NO_WRAP);
                        Log.d("Migration", "Decoded image bytes for: " + foodId + ", size: " + imageBytes.length);
                        saveImageForFood(foodId, imageBytes);

                        // Remove the old base64 field
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageBase64", com.google.firebase.firestore.FieldValue.delete());
                        doc.getReference().update(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("Migration", "Migration complete for: " + foodId))
                                .addOnFailureListener(e ->
                                        Log.e("Migration", "Failed updating Firestore for: " + foodId, e));
                    } catch (IllegalArgumentException e) {
                        Log.e("Migration", "Failed decoding base64 for: " + foodId, e);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Migration", "Failed fetching document for: " + foodId, e));
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
                                "gemini-2.5-flash-image",
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

    public void flagImage(String foodId, String userId, final CompletionCallback callback) {
        DocumentReference foodDocRef = db.collection("food_images").document(foodId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(foodDocRef);
            long flagCount = 0;
            List<String> flaggedBy = new ArrayList<>();

            if (snapshot.exists()) {
                if (snapshot.getLong("flagCount") != null) {
                    flagCount = snapshot.getLong("flagCount");
                }
                if (snapshot.get("flaggedBy") instanceof List) {
                    flaggedBy = (List<String>) snapshot.get("flaggedBy");
                }
            }

            // Check if user already flagged this image
            if (!flaggedBy.contains(userId)) {
                flagCount++;
                flaggedBy.add(userId);
                long finalFlagCount = flagCount;
                List<String> finalFlaggedBy = flaggedBy;
                transaction.set(foodDocRef,
                        new HashMap<String, Object>() {{
                            put("flagCount", finalFlagCount);
                            put("flaggedBy", finalFlaggedBy);
                        }}, SetOptions.merge());
            }
            return flagCount;
        }).addOnSuccessListener(flagCount -> {
            Log.d("FirestoreHelper", "Image flagged successfully. Total flags: " + flagCount);
            callback.onComplete(true, null);
        }).addOnFailureListener(e -> {
            Log.e("FirestoreHelper", "Failed to flag image", e);
            callback.onComplete(false, e);
        });
    }

    public void getFlagCount(String foodId, FirestoreCallback2 callback) {
        db.collection("food_images")
                .document(foodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("flagCount")) {
                        long flagCount = documentSnapshot.getLong("flagCount");
                        callback.onSuccess(flagCount + " flags");
                    } else {
                        callback.onSuccess("0 flags");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error fetching flag count", e);
                    callback.onFailure(e);
                });
    }

    public void unflagImage(String foodId, String userId, final CompletionCallback callback) {
        DocumentReference foodDocRef = db.collection("food_images").document(foodId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(foodDocRef);
            long flagCount = 0;
            List<String> flaggedBy = new ArrayList<>();

            if (snapshot.exists()) {
                if (snapshot.getLong("flagCount") != null) {
                    flagCount = snapshot.getLong("flagCount");
                }
                if (snapshot.get("flaggedBy") instanceof List) {
                    flaggedBy = (List<String>) snapshot.get("flaggedBy");
                }
            }

            // Remove user from flaggedBy list
            if (flaggedBy.contains(userId)) {
                flaggedBy.remove(userId);
                flagCount = Math.max(0, flagCount - 1);
                long finalFlagCount = flagCount;
                List<String> finalFlaggedBy = flaggedBy;
                transaction.set(foodDocRef,
                        new HashMap<String, Object>() {{
                            put("flagCount", finalFlagCount);
                            put("flaggedBy", finalFlaggedBy);
                        }}, SetOptions.merge());
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("FirestoreHelper", "Image unflagged successfully");
            callback.onComplete(true, null);
        }).addOnFailureListener(e -> {
            Log.e("FirestoreHelper", "Failed to unflag image", e);
            callback.onComplete(false, e);
        });
    }

    public void regenerateImageForFood(String foodId, String prompt,
                                       ExpandableRecyclerViewAdapter adapter, FirestoreImageCallback callback) {
        if (adapter != null) {
            adapter.setImageLoading(foodId, true);
        }

        generateNanobananaImage(prompt, new FirestoreImageCallback() {
            @Override
            public void onSuccess(byte[] generatedBytes) {
                if (generatedBytes != null) {
                    // Save to Firestore
                    saveImageForFood(foodId, generatedBytes);

                    // Reset flag metadata
                    resetImageFlags(foodId, new CompletionCallback() {
                        @Override
                        public void onComplete(boolean success, Exception e) {
                            if (adapter != null) {
                                adapter.setImageLoading(foodId, false);
                                adapter.setImageBytes(foodId, generatedBytes);
                            }
                            if (callback != null) {
                                callback.onSuccess(generatedBytes);
                            }
                        }
                    });
                } else {
                    if (adapter != null) {
                        adapter.setImageLoading(foodId, false);
                    }
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirestoreHelper", "Image regeneration failed", e);
                if (adapter != null) {
                    adapter.setImageLoading(foodId, false);
                }
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    public void resetImageFlags(String foodId, CompletionCallback callback) {
        db.collection("food_images")
                .document(foodId)
                .update("flagCount", 0, "flaggedBy", new ArrayList<>())
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreHelper", "Image flags reset for: " + foodId);
                    callback.onComplete(true, null);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Failed to reset flags", e);
                    callback.onComplete(false, e);
                });
    }

    public void generateNutritionForFood(String foodName, FirestoreNutritionCallback callback) {
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                String apiKey = BuildConfig.NANOBANANA_API_KEY;
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    mainThreadHandler.post(() -> callback.onFailure(new Exception("Missing API_KEY")));
                    return;
                }

                Client client = Client.builder().apiKey(apiKey).build();

                String prompt = "Estimate the nutritional details for 1 serving of " + foodName + ". " +
                        "Return ONLY a valid JSON object. No markdown wrappers like ```json. " +
                        "The keys should be standard nutrients (calories, total_fat, saturated_fat, trans_fat, cholesterol, sodium, total_carbohydrate, dietary_fiber, protein, sugar). " +
                        "The value for each key MUST be a stringified JSON object containing 'label', 'value', and 'unit'. " +
                        "Example: {\"calories\": \"{\\\"label\\\": \\\"Calories\\\", \\\"value\\\": \\\"250\\\", \\\"unit\\\": \\\"\\\"}\", \"total_fat\": \"{\\\"label\\\": \\\"Total Fat\\\", \\\"value\\\": \\\"10\\\", \\\"unit\\\": \\\"g\\\"}\"}";

                List<Content> contents = ImmutableList.of(
                        Content.builder()
                                .role("user")
                                .parts(ImmutableList.of(Part.fromText(prompt)))
                                .build()
                );

                GenerateContentConfig config = GenerateContentConfig.builder()
                        .responseModalities(ImmutableList.of("TEXT"))
                        .build();

                GenerateContentResponse response = client.models.generateContent(
                        "gemini-2.5-flash",
                        contents,
                        config
                );

                String textResult = response.text();

                // Parse the response into the Map<String, String>
                Map<String, String> generatedNutrition = new HashMap<>();
                if (textResult != null && !textResult.isEmpty()) {
                    // Clean up markdown just in case the model ignored instructions
                    textResult = textResult.replace("```json", "").replace("```", "").trim();
                    org.json.JSONObject root = new org.json.JSONObject(textResult);
                    java.util.Iterator<String> keys = root.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        generatedNutrition.put(key, root.getString(key));
                    }

                    // Inject the AI Generated flag here
                    generatedNutrition.put("isAiGenerated", "true");
                }

                mainThreadHandler.post(() -> callback.onSuccess(generatedNutrition));

            } catch (Exception e) {
                Log.e("FirestoreHelper", "generateNutritionForFood failed", e);
                mainThreadHandler.post(() -> callback.onFailure(e));
            }
        }).start();
    }

    public void saveNutritionForFood(String foodId, Map<String, String> nutritionMap) {
        if (foodId == null || nutritionMap == null) return;

        db.collection("food_nutrition")
                .document(foodId)
                .set(nutritionMap, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("FirestoreHelper", "Saved nutrition details for " + foodId))
                .addOnFailureListener(e ->
                        Log.e("FirestoreHelper", "Failed to save nutrition details for " + foodId, e));
    }

    public void loadNutritionForFood(String foodId, FirestoreNutritionCallback callback) {
        if (foodId == null) {
            callback.onSuccess(null);
            return;
        }

        db.collection("food_nutrition")
                .document(foodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        Map<String, Object> rawData = documentSnapshot.getData();
                        Map<String, String> nutritionMap = new HashMap<>();

                        // Safely cast Firestore data to Map<String, String>
                        for (Map.Entry<String, Object> entry : rawData.entrySet()) {
                            if (entry.getValue() instanceof String) {
                                nutritionMap.put(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        callback.onSuccess(nutritionMap);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error loading nutrition details for " + foodId, e);
                    callback.onFailure(e);
                });
    }

}
