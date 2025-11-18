package edu.vassar.cmpu203.vassareats.model;

public class MealTime {
    private String mealName;
    private String timeRange;
    private int backgroundDrawableId; // To hold a reference like R.drawable.breakfast_bg

    public MealTime(String mealName, String timeRange, int backgroundDrawableId) {
        this.mealName = mealName;
        this.timeRange = timeRange;
        this.backgroundDrawableId = backgroundDrawableId;
    }

    public String getMealName() {
        return mealName;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public int getBackgroundDrawableId() {
        return backgroundDrawableId;
    }
}

