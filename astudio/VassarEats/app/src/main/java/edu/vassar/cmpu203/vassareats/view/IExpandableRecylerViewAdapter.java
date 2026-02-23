package edu.vassar.cmpu203.vassareats.view;

import java.util.List;
import java.util.Set;

public interface IExpandableRecylerViewAdapter {
    void setImageBytes(String foodId, byte[] imageBytes);

    interface Listener {
        void onLikeClicked(String foodId);
        void onDislikeClicked(String foodId);
        void onParentToggle(String parentId);
    }

    void setFlatItems(List<Object> flatItems);
    void setLikedItems(Set<String> likedItems);
    void setDislikedItems(Set<String> dislikedItems);
}
