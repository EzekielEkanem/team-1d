package edu.vassar.cmpu203.vassareats.model;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class GlideConfiguration extends AppGlideModule {
    private static final int MEMORY_CACHE_SIZE = 50 * 1024 * 1024; // 50 MB RAM cache
    private static final int DISK_CACHE_SIZE = 500 * 1024 * 1024; // 500 MB disk cache

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Configure memory cache
        builder.setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE));

        // Configure disk cache (internal storage)
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, "glide_cache", DISK_CACHE_SIZE)
        );

        // Set default error handling and timeout
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .timeout(10000) // 10 second timeout
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide, com.bumptech.glide.Registry registry) {
        // Optional: register custom components if needed
    }
}
