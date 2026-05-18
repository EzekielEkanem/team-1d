package edu.vassar.cmpu203.vassareats.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.model.DiningStation;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;
import edu.vassar.cmpu203.vassareats.model.ParentItem;

public class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IExpandableRecylerViewAdapter {

    private static final int TYPE_MEAL_TYPE = 0;
    private static final int TYPE_FOOD_ITEM = 3;
    private static final int TYPE_MEAL_TYPE_SECTION = 1;
    private static final int TYPE_DINING_STATION = 2;
    private final Map<String, String> imageUrls = new HashMap<>();
    private List<Object> items;
    private final Map<String, byte[]> imageBytesMap = new HashMap<>();
    private Set<String> likedItems;
    private Set<String> dislikedItems;
    private Set<String> reportedItems;
    private final Set<String> loadingImageIds = ConcurrentHashMap.newKeySet();

    Listener listener;
    Context context;

    public ExpandableRecyclerViewAdapter(Context context, List<Object> flatItems, Listener listener, Set<String> likedItems, Set<String> dislikedItems) {
        this.context = context;
        this.listener = listener;
        this.items = (flatItems != null) ? new ArrayList<>(flatItems) : new ArrayList<>();
        this.likedItems = (likedItems != null) ? Collections.unmodifiableSet(new HashSet<>(likedItems)) : Collections.emptySet();
        this.dislikedItems = (dislikedItems != null) ? Collections.unmodifiableSet(new HashSet<>(dislikedItems)) : Collections.emptySet();
        this.reportedItems = (reportedItems != null) ? Collections.unmodifiableSet(new HashSet<>(reportedItems)) : Collections.emptySet();
    }

    @Override
    public void setFlatItems(List<Object> flatItems) {
        this.items = (flatItems != null) ? new ArrayList<>(flatItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    private int findPositionByFoodId(String foodId) {
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item instanceof FoodItem && foodId.equals(((FoodItem) item).getFoodId())) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    @Override
    public void setImageBytes(String foodId, byte[] imageBytes) {
        if (foodId == null || imageBytes == null) return;
        loadingImageIds.remove(foodId); // stop shimmer when image arrives
        imageBytesMap.put(foodId, imageBytes);

        int position = findPositionByFoodId(foodId);
        if (position == RecyclerView.NO_POSITION) return;

        notifyItemChanged(position, imageBytes);
    }

    @Override
    public void setLikedItems(Set<String> likedItems) {
        this.likedItems = (likedItems != null) ? Collections.unmodifiableSet(new HashSet<>(likedItems)) : Collections.emptySet();
        notifyDataSetChanged();
    }

    @Override
    public void setDislikedItems(Set<String> dislikedItems) {
        this.dislikedItems = (dislikedItems != null) ? Collections.unmodifiableSet(new HashSet<>(dislikedItems)) : Collections.emptySet();
        notifyDataSetChanged();
    }

    @Override
    public void setReportedItems(Set<String> reportedItems) {
        this.reportedItems = (reportedItems != null) ? Collections.unmodifiableSet(new HashSet<>(reportedItems)) : Collections.emptySet();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof ParentItem) {
            return TYPE_MEAL_TYPE;
        } else if (item instanceof MealTypeSection) {
            return TYPE_MEAL_TYPE_SECTION;
        } else if (item instanceof FoodItem) {
            return TYPE_FOOD_ITEM;
        } else {
            return TYPE_DINING_STATION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_MEAL_TYPE) {
            View view = inflater.inflate(R.layout.expandable_list_meal_type, parent, false);
            return new MealTypeViewHolder(view);
        } else if (viewType == TYPE_FOOD_ITEM) {
            View view = inflater.inflate(R.layout.expandable_list_food_item, parent, false);
            return new FoodItemViewHolder(view);
        } else if (viewType == TYPE_MEAL_TYPE_SECTION) {
            View view = inflater.inflate(R.layout.expandable_list_meal_type_section, parent, false);
            return new MealTypeSectionViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.expandable_list_dining_station, parent, false);
            return new DiningStationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && holder instanceof FoodItemViewHolder) {
            Object payload = payloads.get(0);
            if (payload instanceof byte[]) {
                byte[] imageBytes = (byte[]) payload;

                Object item = items.get(position);
                if (item instanceof FoodItem) {
                    imageBytesMap.put(((FoodItem) item).getFoodId(), imageBytes);
                }

                bindFoodImage((FoodItemViewHolder) holder, (FoodItem) item);
                return; // Image updated, no need for full rebind
            }
        }
        // Fallback to full rebind
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MEAL_TYPE) {
            ParentItem parentItem = (ParentItem) items.get(position);
            MealTypeViewHolder parentHolder = (MealTypeViewHolder) holder;
            parentHolder.mealTypeName.setText(parentItem.getTitle());

            parentHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    try { listener.onParentToggle(parentItem.getTitle()); }
                    catch (NoSuchMethodError ignored) {}
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOD_ITEM) {
            FoodItem foodItem = (FoodItem) items.get(position);
            FoodItemViewHolder foodItemHolder = (FoodItemViewHolder) holder;
            foodItemHolder.foodItemName.setText(foodItem.getFoodItemName());

            bindFoodImage(foodItemHolder, foodItem);

            // Dynamically check if the item is liked (safe against null)
            boolean isLiked = likedItems != null && likedItems.contains(foodItem.getFoodId());
            boolean isDisliked = dislikedItems != null && dislikedItems.contains(foodItem.getFoodId());
            boolean isReported = reportedItems != null && reportedItems.contains(foodItem.getFoodId());

            updateLikeButton(foodItemHolder.likeButton, isLiked);
            updateDislikeButton(foodItemHolder.dislikeButton, isDisliked);
            updateReportButton(foodItemHolder.reportImageButton, isReported);

            // Click listener for the like button
            foodItemHolder.likeButton.setOnClickListener(buttonView -> {
                if (listener != null) {
                    try { listener.onLikeClicked(foodItem.getFoodId()); } catch (NoSuchMethodError ignored) {}
                }
            });

            foodItemHolder.dislikeButton.setOnClickListener(buttonView -> {
                if (listener != null) {
                    try { listener.onDislikeClicked(foodItem.getFoodId()); } catch (NoSuchMethodError ignored) {}
                }
            });

            foodItemHolder.reportImageButton.setOnClickListener(v -> {
                if (listener != null) {
                    try { listener.onReportImageClicked(foodItem.getFoodId()); } catch (NoSuchMethodError ignored) {}
                }
            });

            foodItemHolder.nutritionButton.setOnClickListener(v -> {
                if (listener != null) {
                    try { listener.onNutritionButtonClicked(foodItem); } catch (NoSuchMethodError ignored) {}
                }
            });

            foodItemHolder.likesCount.setText("");
        } else if (getItemViewType(position) == TYPE_MEAL_TYPE_SECTION) {
            MealTypeSection mealTypeSection = (MealTypeSection) items.get(position);
            MealTypeSectionViewHolder childHolder = (MealTypeSectionViewHolder) holder;
            childHolder.mealTypeSectionName.setText(mealTypeSection.getMealTypeSectionName());
        } else {
            DiningStation diningStation = (DiningStation) items.get(position);
            DiningStationViewHolder diningStationHolder = (DiningStationViewHolder) holder;
            diningStationHolder.diningStationName.setText(diningStation.getDiningStationName());
        }
    }


    private void updateLikeButton(ImageButton likeButton, boolean isLiked) {
        int drawableRes = isLiked ? R.drawable.ic_thumb_up_filled : R.drawable.ic_thumb_up_outline;
        likeButton.setImageResource(drawableRes);

        int tintColor = isLiked
                ? ContextCompat.getColor(context, R.color.purple_700)
                : ContextCompat.getColor(context, R.color.gray);
        likeButton.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);

        likeButton.setContentDescription(isLiked
                ? context.getString(R.string.unlike)
                : context.getString(R.string.like));
    }

    private void updateDislikeButton(ImageButton dislikeButton, boolean isDisliked) {
        int drawableRes = isDisliked ? R.drawable.ic_thumb_down_filled : R.drawable.ic_thumb_down_outline;
        dislikeButton.setImageResource((drawableRes != 0) ? drawableRes : android.R.drawable.btn_minus);

        int tintColor = isDisliked
                ? ContextCompat.getColor(context, R.color.purple_700)
                : ContextCompat.getColor(context, R.color.gray);
        dislikeButton.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);

        dislikeButton.setContentDescription(isDisliked
                ? context.getString(R.string.undislike)
                : context.getString(R.string.dislike));
    }

    private void updateReportButton(ImageButton reportButton, boolean isReported) {
        int drawableRes = isReported ? R.drawable.ic_flag_filled : R.drawable.ic_flag_outline;
        reportButton.setImageResource(drawableRes);

        int tintColor = isReported
                ? ContextCompat.getColor(context, R.color.purple_700) // Or your desired color for reported items
                : ContextCompat.getColor(context, R.color.gray);
        reportButton.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);

        reportButton.setContentDescription(isReported
                ? context.getString(R.string.unreport) // Make sure to add this to strings.xml
                : context.getString(R.string.report)); // Make sure to add this to strings.xml
    }


    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }

    static class MealTypeViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeName;

        public MealTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTypeName = itemView.findViewById(R.id.mealTypeName);
        }
    }

    static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFood;
        TextView foodItemName;
        ImageButton likeButton;
        ImageButton dislikeButton;
        TextView likesCount;
        ShimmerFrameLayout imageShimmer;
        ImageButton reportImageButton;
        ImageButton nutritionButton;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodItemName = itemView.findViewById(R.id.foodItemName);
            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
            likesCount = itemView.findViewById(R.id.likesCount);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            imageShimmer = itemView.findViewById(R.id.imageShimmer);
            reportImageButton = itemView.findViewById(R.id.reportImageButton);
            nutritionButton = itemView.findViewById(R.id.nutritionButton);
        }
    }

    static class MealTypeSectionViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeSectionName;

        public MealTypeSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTypeSectionName = itemView.findViewById(R.id.mealTypeSectionName);
        }
    }

    static class DiningStationViewHolder extends RecyclerView.ViewHolder {
        TextView diningStationName;

        public DiningStationViewHolder(@NonNull View itemView) {
            super(itemView);
            diningStationName = itemView.findViewById(R.id.diningStationName);
        }
    }

    public void bindChild(FoodItemViewHolder holder, FoodItem child) {
        if (holder == null || child == null) return;

        // Text
        holder.foodItemName.setText(child.getFoodItemName());

        // Image
        String url = imageUrls.get(child.getFoodId());
        if (url != null && !url.isEmpty()) {
            try {
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into((android.widget.ImageView) ((View) holder.itemView).findViewById(R.id.imageViewFood));
            } catch (Exception e) {
                // Glide might not be available during some IDE inspections; fallback to placeholder
                android.widget.ImageView iv = ((View) holder.itemView).findViewById(R.id.imageViewFood);
                if (iv != null) iv.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            android.widget.ImageView iv = ((View) holder.itemView).findViewById(R.id.imageViewFood);
            if (iv != null) iv.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Visual like/dislike state
        boolean isLiked = likedItems != null && likedItems.contains(child.getFoodId());
        boolean isDisliked = dislikedItems != null && dislikedItems.contains(child.getFoodId());
        boolean isReported = reportedItems != null && reportedItems.contains(child.getFoodId());

        updateLikeButton(holder.likeButton, isLiked);
        updateDislikeButton(holder.dislikeButton, isDisliked);
        updateReportButton(holder.reportImageButton, isReported);


        // Likes count (keep existing behavior)
        if (holder.likesCount != null) {
            holder.likesCount.setText("");
        }

        // Click handlers (mirror logic used elsewhere in adapter)
        holder.likeButton.setOnClickListener(v -> {
            if (listener != null) {
                try { listener.onLikeClicked(child.getFoodId()); } catch (NoSuchMethodError ignored) {}
            }
        });

        holder.dislikeButton.setOnClickListener(v -> {
            if (listener != null) {
                try { listener.onDislikeClicked(child.getFoodId()); } catch (NoSuchMethodError ignored) {}
            }
        });
    }

    // public API
    public void setImageLoading(String foodId, boolean isLoading) {
        if (foodId == null) return;
        if (isLoading) loadingImageIds.add(foodId);
        else loadingImageIds.remove(foodId);

        int position = findPositionByFoodId(foodId);
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position, "loading_state");
        }
    }

    // helper
    private void bindFoodImage(FoodItemViewHolder holder, FoodItem foodItem) {
        byte[] imageBytes = imageBytesMap.get(foodItem.getFoodId());
        boolean isLoading = loadingImageIds.contains(foodItem.getFoodId());

        if (imageBytes != null) {
            holder.imageShimmer.stopShimmer();
            holder.imageShimmer.setVisibility(View.GONE);
            holder.imageViewFood.setVisibility(View.VISIBLE);

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageViewFood.setImageBitmap(bitmap);
        } else if (isLoading) {
            holder.imageViewFood.setVisibility(View.INVISIBLE);
            holder.imageShimmer.setVisibility(View.VISIBLE);
            holder.imageShimmer.startShimmer();
        } else {
            holder.imageShimmer.stopShimmer();
            holder.imageShimmer.setVisibility(View.GONE);
            holder.imageViewFood.setVisibility(View.VISIBLE);
            holder.imageViewFood.setImageResource(R.drawable.ic_image_placeholder);
        }
    }
}
