package edu.vassar.cmpu203.vassareats.view;

import android.view.View;

import java.util.List;

public interface IExpandableRecylerViewAdapter {
    interface Listener {}

    void setParentItems(List<ParentItem> parentItems);
}
