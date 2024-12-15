package edu.vassar.cmpu203.vassareats.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            Log.d("MainActivity", "This item liked state: " + foodItem.getFoodId() + likedItems.contains(foodItem.getFoodId()));

            // Check if the item is liked
            boolean isLiked = likedItems.contains(foodItem.getFoodId());

            // Set initial state
            updateLikeButton(foodItemHolder.likeButton, isLiked);

            foodItemHolder.likeButton.setOnClickListener((buttonView) -> {
                boolean newState = !likedItems.contains(foodItem.getFoodId());
                if (newState) {
                    likedItems.add(foodItem.getFoodId());
                } else {
                    likedItems.remove(foodItem.getFoodId());
                }

                // Update button visuals
                updateLikeButton(foodItemHolder.likeButton, newState);

                // Notify MainActivity
                listener.updateLikedItems(foodItem.getFoodId(), newState);
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

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodItemName = itemView.findViewById(R.id.foodItemName);
            likeButton = itemView.findViewById(R.id.likeButton);
//            dislikeButton = itemView.findViewById(R.id.dislikeButton);
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
