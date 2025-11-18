package edu.vassar.cmpu203.vassareats.view;

import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.FirestoreHelper;

public interface IExpandableRecylerViewAdapter {
    interface Listener {
//        void updateLikedItems(String foodItemName, boolean isLiked);
//        void updateLikeCount(String foodId, boolean isLiked);
//        void getLikeCount(String foodId, FirestoreHelper.FirestoreCallback2 firestoreCallback);

        void onLikeClicked(String foodId, boolean isNowLiked);
    }

    void setParentItems(List<ParentItem> parentItems);
    void setLikedItems(Set<String> likedItems);
}
