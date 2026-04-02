package edu.vassar.cmpu203.vassareats.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageCacheManager {
    private static final String TAG = "ImageCacheManager";
    private final Context context;
    private final int targetWidth;
    private final int targetHeight;

    public interface ImageLoadCallback {
        void onSuccess(byte[] imageBytes);
        void onFailure(Exception e);
    }

    public ImageCacheManager(Context context, int targetWidth, int targetHeight) {
        this.context = context;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    /**
     * Load image from URL with local caching, transformation, and memory safety.
     */
    public void loadImageWithCache(String imageUrl, ImageLoadCallback callback) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Image URL is null or empty"));
            return;
        }

        try {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache to both disk and memory
                    .override(targetWidth, targetHeight) // Transform to target size
                    .centerCrop(); // Crop to fit dimensions

            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                // Convert Bitmap to byte array
                                byte[] imageBytes = bitmapToByteArray(resource);
                                callback.onSuccess(imageBytes);
                                Log.d(TAG, "Image loaded and cached successfully: " + imageUrl);
                            } catch (OutOfMemoryError e) {
                                Log.e(TAG, "OutOfMemoryError while converting bitmap", e);
                                callback.onFailure(new Exception("Out of memory", e));
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable android.graphics.drawable.Drawable errorDrawable) {
                            Log.e(TAG, "Failed to load image: " + imageUrl);
                            callback.onFailure(new Exception("Image load failed"));
                        }
                    });
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError during image load request", e);
            callback.onFailure(new Exception("Out of memory", e));
            // Clear memory cache to recover
            clearMemoryCache();
        } catch (Exception e) {
            Log.e(TAG, "Error loading image", e);
            callback.onFailure(e);
        }
    }

    /**
     * Convert Bitmap to byte array (JPEG format for smaller size).
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream); // 85% quality
        return outputStream.toByteArray();
    }

    /**
     * Clear memory cache to prevent OutOfMemoryError.
     */
    public void clearMemoryCache() {
        try {
            Glide.get(context).clearMemory();
            Log.d(TAG, "Memory cache cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing memory cache", e);
        }
    }

    /**
     * Clear disk cache (runs in background).
     */
    public void clearDiskCache() {
        new Thread(() -> {
            try {
                Glide.get(context).clearDiskCache();
                Log.d(TAG, "Disk cache cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing disk cache", e);
            }
        }).start();
    }

    /**
     * Preload image into cache without displaying.
     */
    public void preloadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        try {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(targetWidth, targetHeight)
                    .centerCrop();

            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .preload();

            Log.d(TAG, "Image preloaded: " + imageUrl);
        } catch (Exception e) {
            Log.e(TAG, "Error preloading image", e);
        }
    }
}

