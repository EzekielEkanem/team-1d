// java
package edu.vassar.cmpu203.vassareats.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.FirestoreHelper;
import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.databinding.ActivityMainBinding;
import edu.vassar.cmpu203.vassareats.model.DiningStation;
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.MealTypeSection;

public class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IExpandableRecylerViewAdapter {

    private static final int TYPE_MEAL_TYPE = 0;
    private static final int TYPE_FOOD_ITEM = 3;
    private static final int TYPE_MEAL_TYPE_SECTION = 1;
    private static final int TYPE_DINING_STATION = 2;

    ActivityMainBinding binding;
    Listener listener;
    Context context;
    Set<String> likedItems;
    Set<String> dislikedItems;

    private List<Object> items;
    private List<ParentItem> parentItemList;

    public ExpandableRecyclerViewAdapter(Context context, List<ParentItem> parentItemList, Listener listener, Set<String> likedItems) {
        this.context = context;
        this.listener = listener;
        this.likedItems = (likedItems != null) ? likedItems : new HashSet<>();
        this.dislikedItems = (dislikedItems != null) ? dislikedItems : new HashSet<>();
        this.parentItemList = parentItemList;
        setParentItems(parentItemList);
    }

    public void setParentItems(List<ParentItem> parentItems) {
        this.items = new ArrayList<>();
        if (parentItems != null) {
            for (ParentItem parent : parentItems) {
                items.add(parent);
                if (parent.isExpanded()) {
                    items.addAll(parent.getChildItems());
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setLikedItems(Set<String> likedItems) {
        this.likedItems = (likedItems != null) ? likedItems : new HashSet<>();
        notifyDataSetChanged();
    }

    public void setDislikedItems(Set<String> dislikedItems) {
        this.dislikedItems = (dislikedItems != null) ? dislikedItems : new HashSet<>();
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MEAL_TYPE) {
            ParentItem parentItem = (ParentItem) items.get(position);
            MealTypeViewHolder parentHolder = (MealTypeViewHolder) holder;
            parentHolder.mealTypeName.setText(parentItem.getTitle());

            parentHolder.itemView.setOnClickListener(v -> {
                parentItem.setExpanded(!parentItem.isExpanded());
                updateItems();
            });
        } else if (getItemViewType(position) == TYPE_FOOD_ITEM) {
            FoodItem foodItem = (FoodItem) items.get(position);
            FoodItemViewHolder foodItemHolder = (FoodItemViewHolder) holder;
            foodItemHolder.foodItemName.setText(foodItem.getFoodItemName());

            // Dynamically check if the item is liked (safe against null)
            boolean isLiked = likedItems != null && likedItems.contains(foodItem.getFoodId());
            boolean isDisliked = dislikedItems != null && dislikedItems.contains(foodItem.getFoodId());

            updateLikeButton(foodItemHolder.likeButton, isLiked);
            updateDislikeButton(foodItemHolder.dislikeButton, isDisliked);

            // Click listener for the like button
            foodItemHolder.likeButton.setOnClickListener(buttonView -> {
                if (likedItems == null) likedItems = new HashSet<>();
                if (dislikedItems == null) dislikedItems = new HashSet<>();

                boolean nowLiked = !likedItems.contains(foodItem.getFoodId());
                if (nowLiked) {
                    likedItems.add(foodItem.getFoodId());
                    // remove dislike if present
                    if (dislikedItems.remove(foodItem.getFoodId())) {
                        updateDislikeButton(foodItemHolder.dislikeButton, false);
                        if (listener != null) {
                            // notify dislike removed (optional)
                            try { listener.onDislikeClicked(foodItem.getFoodId(), false); } catch (NoSuchMethodError ignored) {}
                        }
                    }
                } else {
                    likedItems.remove(foodItem.getFoodId());
                }

                updateLikeButton(foodItemHolder.likeButton, nowLiked);
                if (listener != null) {
                    try { listener.onLikeClicked(foodItem.getFoodId(), nowLiked); } catch (NoSuchMethodError ignored) {}
                }
            });

            foodItemHolder.dislikeButton.setOnClickListener(buttonView -> {
                if (likedItems == null) likedItems = new HashSet<>();
                if (dislikedItems == null) dislikedItems = new HashSet<>();

                boolean nowDisliked = !dislikedItems.contains(foodItem.getFoodId());
                if (nowDisliked) {
                    dislikedItems.add(foodItem.getFoodId());
                    // remove like if present
                    if (likedItems.remove(foodItem.getFoodId())) {
                        updateLikeButton(foodItemHolder.likeButton, false);
                        if (listener != null) {
                            try { listener.onLikeClicked(foodItem.getFoodId(), false); } catch (NoSuchMethodError ignored) {}
                        }
                    }
                } else {
                    dislikedItems.remove(foodItem.getFoodId());
                }

                updateDislikeButton(foodItemHolder.dislikeButton, nowDisliked);
                if (listener != null) {
                    try { listener.onDislikeClicked(foodItem.getFoodId(), nowDisliked); } catch (NoSuchMethodError ignored) {}
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
                ? context.getString(R.string.undislike)  // add this string to resources
                : context.getString(R.string.dislike));
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }

    private void updateItems() {
        List<Object> updatedItems = new ArrayList<>();
        if (items == null) {
            items = updatedItems;
            notifyDataSetChanged();
            return;
        }
        for (Object item : items) {
            if (item instanceof ParentItem) {
                ParentItem parentItem = (ParentItem) item;
                updatedItems.add(parentItem);
                if (parentItem.isExpanded()) {
                    updatedItems.addAll(parentItem.getChildItems());
                }
            }
        }
        items = updatedItems;
        notifyDataSetChanged();
    }

    static class MealTypeViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeName;

        public MealTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTypeName = itemView.findViewById(R.id.mealTypeName);
        }
    }

    static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        TextView foodItemName;
        ImageButton likeButton;
        ImageButton dislikeButton;
        TextView likesCount;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodItemName = itemView.findViewById(R.id.foodItemName);
            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
            likesCount = itemView.findViewById(R.id.likesCount);
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
}
