package edu.vassar.cmpu203.vassareats.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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

    private List<Object> items;

    public ExpandableRecyclerViewAdapter(Context context, Listener listener, Set<String> likedItems) {
        this.context = context;
        this.binding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        this.listener = listener;
        this.likedItems = likedItems;
    }

    public void setParentItems(List<ParentItem> parentItems) {
        this.items = new ArrayList<>();
        for (ParentItem parent : parentItems) {
            items.add(parent);
            if (parent.isExpanded()) {
                items.addAll(parent.getChildItems());
            }
        }
        notifyDataSetChanged();
    }

    public void setLikedItems(Set<String> likedItems) {
        this.likedItems = likedItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ParentItem) {
            return TYPE_MEAL_TYPE;
        } else if (items.get(position) instanceof MealTypeSection) {
            return TYPE_MEAL_TYPE_SECTION;
        } else if (items.get(position) instanceof FoodItem) {
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

            // Dynamically check if the item is liked
            boolean isLiked = likedItems.contains(foodItem.getFoodId());
            updateLikeButton(foodItemHolder.likeButton, isLiked);

            // Fetch and display like count dynamically
            listener.getLikeCount(foodItem.getFoodId(), new FirestoreHelper.FirestoreCallback2() {
                @Override
                public void onSuccess(String likedItems) {
                    foodItemHolder.likesCount.setText(likedItems);
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Exception e) {
                    foodItemHolder.likesCount.setText("Error");
                }
            });

            // Click listener for the like button
            foodItemHolder.likeButton.setOnClickListener((buttonView) -> {
                boolean newState = !likedItems.contains(foodItem.getFoodId());

                // Update likedItems set
                if (newState) {
                    likedItems.add(foodItem.getFoodId());
                } else {
                    likedItems.remove(foodItem.getFoodId());
                }

                // Notify MainActivity
                listener.updateLikedItems(foodItem.getFoodId(), newState);
                listener.updateLikeCount(foodItem.getFoodId(), newState);

                // Update button visuals
                updateLikeButton(foodItemHolder.likeButton, newState);

                // Fetch and update the like count again
                listener.getLikeCount(foodItem.getFoodId(), new FirestoreHelper.FirestoreCallback2() {
                    @Override
                    public void onSuccess(String likedItems) {
                        foodItemHolder.likesCount.setText(likedItems);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(Exception e) {
                        foodItemHolder.likesCount.setText("Error");
                    }
                });
            });
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

    private void updateLikeButton(TextView likeButton, boolean isLiked) {
        likeButton.setText(isLiked ? "Liked" : "Like");
        likeButton.setBackgroundResource(isLiked ? R.color.purple_700 : R.color.gray);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updateItems() {
        List<Object> updatedItems = new ArrayList<>();
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
        TextView likeButton;
        TextView likesCount;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodItemName = itemView.findViewById(R.id.foodItemName);
            likeButton = itemView.findViewById(R.id.likeButton);
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
