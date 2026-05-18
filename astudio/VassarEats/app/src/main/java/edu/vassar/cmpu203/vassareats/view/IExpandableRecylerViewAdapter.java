package edu.vassar.cmpu203.vassareats.view;

import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.model.FoodItem;

public interface IExpandableRecylerViewAdapter {
    void setImageBytes(String foodId, byte[] imageBytes);

    interface Listener {
        void onLikeClicked(String foodId);
        void onDislikeClicked(String foodId);
        void onParentToggle(String parentId);
        void onReportImageClicked(String foodId);
        void onNutritionButtonClicked(FoodItem foodItem);

    }

    void setFlatItems(List<Object> flatItems);
    void setLikedItems(Set<String> likedItems);
    void setDislikedItems(Set<String> dislikedItems);
    void setReportedItems(Set<String> reportedImages);
}
