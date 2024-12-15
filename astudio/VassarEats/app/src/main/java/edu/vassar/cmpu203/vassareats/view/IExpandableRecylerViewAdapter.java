package edu.vassar.cmpu203.vassareats.view;

import java.util.List;
import java.util.Set;

public interface IExpandableRecylerViewAdapter {
    interface Listener {
        void updateLikedItems(String foodItemName, boolean isLiked);
    }

    void setParentItems(List<ParentItem> parentItems);
    void setLikedItems(Set<String> likedItems);
}
