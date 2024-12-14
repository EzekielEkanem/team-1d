package edu.vassar.cmpu203.vassareats.view;

import java.util.List;

public interface IExpandableRecylerViewAdapter {
    interface Listener {}

    void setParentItems(List<ParentItem> parentItems);
}
