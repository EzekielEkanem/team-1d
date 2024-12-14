package edu.vassar.cmpu203.vassareats.model;

import java.util.List;

public class ParentItem {
    private String title; // Parent item title
    private List<String> childItems; // List of child items
    private boolean isExpanded; // To track expanded state

    public ParentItem(String title, List<String> childItems) {
        this.title = title;
        this.childItems = childItems;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getChildItems() {
        return childItems;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}

