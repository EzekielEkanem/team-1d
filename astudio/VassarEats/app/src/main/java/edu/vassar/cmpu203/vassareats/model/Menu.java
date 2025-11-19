package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.vassareats.view.ParentItem;

public class Menu {
    private Preference preference;
    private List<MealType> originalMenu;
    private List<MealType> filteredMenu;
    private LocalDate currentDate;
    private Request request;
    private Integer diningLocation;

    /**
     *
     * @throws ParseException
     * @throws JSONException
     */
    public Menu() throws ParseException, JSONException {
        currentDate = LocalDate.now();
        request = new Request();
        preference = new Preference();
        diningLocation = 0;

        originalMenu = request.getJavaMenu(currentDate, diningLocation);
        filteredMenu = originalMenu;
    }

    public void updateDate(LocalDate localDate) throws JSONException, ParseException {
        if (!currentDate.equals(localDate)) {
            currentDate = localDate;
            originalMenu = request.getJavaMenu(currentDate, diningLocation);
            updateFilteredMenu();
        }
    }

    public void updateLocation(Integer diningLoc) throws JSONException, ParseException {
        if (!(diningLocation == diningLoc)) {
            diningLocation = diningLoc;
            originalMenu = request.getJavaMenu(currentDate, diningLocation);
            updateFilteredMenu();
        }
    }

    public List<MealType> getMenu() {
        return originalMenu;
    }

    public List<MealType> getFilteredMealTypes() {
        return filteredMenu;
    }

    private void updateFilteredMenu() {
        List<String> preferencesList = preference.getPreferences();

        filteredMenu = new ArrayList<MealType>();

        int menuSize = 0;

        for (MealType mealType : originalMenu) {

            MealType newMealType = new MealType(mealType.getMealTypeName());

            filteredMenu.add(newMealType);

            menuSize ++;

            int currentMealTypeMenuCount = menuSize;

            for (MealTypeSection mealTypeSection : mealType.getMealTypeSections()) {

                MealTypeSection newMealTypeSection = new MealTypeSection(mealTypeSection.getMealTypeSectionName());

                newMealType.addMealTypeSection(newMealTypeSection);

                menuSize ++;

                int currentMealTypeSectionMenuCount = menuSize;

                for (DiningStation diningStation : mealTypeSection.getDiningStations()) {

                    DiningStation newDiningStation = new DiningStation(diningStation.getDiningStationName());

                    newMealTypeSection.addDiningStation(newDiningStation);

                    menuSize ++;

                    int currentDiningStationMenuCount = menuSize;

                    for (FoodItem foodItem : diningStation.getFoodItems()) {

                        if (preferencesList.isEmpty()) {
                            FoodItem newFoodItem = new FoodItem(foodItem.getFoodItemName(), foodItem.getFoodId(), foodItem.getDietLabels());

                            newDiningStation.addFoodItem(newFoodItem);

                            menuSize ++;
                        } else {
                            for (String dietLabel : foodItem.getDietLabels()) {
                                if (preferencesList.contains(dietLabel)) {
                                    FoodItem newFoodItem = new FoodItem(foodItem.getFoodItemName(), foodItem.getFoodId(), foodItem.getDietLabels());

                                    newDiningStation.addFoodItem(newFoodItem);

                                    menuSize ++;

                                    break;
                                }
                            }
                        }
                    }

                    if (menuSize == currentDiningStationMenuCount) {
                        newMealTypeSection.getDiningStations().remove(newMealTypeSection.getDiningStations().size() - 1);
                        menuSize --;
                    }
                }

                if (menuSize == currentMealTypeSectionMenuCount) {
                    newMealType.getMealTypeSections().remove(newMealType.getMealTypeSections().size() - 1);
                    menuSize --;
                }
            }

            if (menuSize == currentMealTypeMenuCount) {
                filteredMenu.remove(filteredMenu.size() - 1);
                menuSize --;
            }
        }
    }

    public void changePreferences(List<Preference.Preferences> preferences) {
        preference.setPreferences(preferences);

        updateFilteredMenu();
    }

    public List<String> getPreferences() {
        return preference.getPreferences();
    }

    private List<ParentItem> getParentItems(List<MealType> menu) {
        List<ParentItem> parentItems = new ArrayList<>();

        for (MealType mealType : menu) {

            List<Object> mealInfo = new ArrayList<Object>();
            for (MealTypeSection mealTypeSection : mealType.getMealTypeSections()) {
                mealInfo.add(mealTypeSection);

                for (DiningStation diningStation : mealTypeSection.getDiningStations()) {
                    mealInfo.add(diningStation);

                    mealInfo.addAll(diningStation.getFoodItems());
                }
            }

            ParentItem parentItem = new ParentItem(mealType.getMealTypeName(), mealInfo);
            parentItems.add(parentItem);
        }
        return parentItems;
    }

    public List<ParentItem> getFilteredMenuParentItems() {
        return getParentItems(filteredMenu);
    }

    public String toString () {
        String returnString = "";

        returnString += "**************************************************************************\n";
        returnString += "                               Menu Items                                 \n";
        returnString += "**************************************************************************\n";

        for (MealType mealType : originalMenu) {
            returnString += mealType.toString() + "\n";
            returnString += "**************************************************************************\n";
        }

        return returnString;
    }

    public void resetFilters() {
        preference.clearPreferences();
        filteredMenu = originalMenu;
    }

    /**
     * Moves the current date forward by one day.
     */
    public void goToNextDay() throws JSONException, ParseException {
        this.updateDate(this.currentDate.plusDays(1));
    }

    /**
     * Moves the current date back by one day.
     */
    public void goToPreviousDay() throws JSONException, ParseException {
        this.updateDate(this.currentDate.minusDays(1));
    }

    /**
     * Public getter for the controller to access the current date.
     */
    public LocalDate getCurrentDate() {
        return this.currentDate;
    }


    /**
     * Filters and returns only the ParentItems that match a specific meal type.
     * @param mealName The name of the meal to filter by (e.g., "Breakfast").
     * @return A list containing only the matching ParentItem.
     */
    public ArrayList<ParentItem> getItemsForMealType(String mealName) {
        ArrayList<ParentItem> allItems = (ArrayList<ParentItem>) getFilteredMenuParentItems(); // Gets all items for the day/location
        ArrayList<ParentItem> result = new ArrayList<>();

        for (ParentItem parent : allItems) {
            if (parent.getTitle().equalsIgnoreCase(mealName)) {
                result.add(parent);
                break; // Assuming meal names are unique, we can stop once found
            }
        }
        return result;
    }

    public Integer getCurrentLocation() {
        return diningLocation;
    }
}
