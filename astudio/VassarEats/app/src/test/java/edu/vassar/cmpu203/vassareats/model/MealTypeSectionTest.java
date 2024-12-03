package edu.vassar.cmpu203.vassareats.model;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MealTypeSectionTest {
    MealTypeSection mealTypeSection;
    DiningStation diningSection;

    /**
     * Default constructor for test class MealTypeSectionTest
     */
    public MealTypeSectionTest() throws JSONException {
        HashSet<HashMap<String, JSONObject>> foodItem = new HashSet<HashMap<String, JSONObject>>();
        HashMap<String, JSONObject> foodItemHashMap = new HashMap<String, JSONObject>();
        JSONObject value = getJsonObject();
        foodItemHashMap.put("27444491", value);
        foodItem.add(foodItemHashMap);
        ArrayList<Integer> preferenceList = new ArrayList<Integer>();
        preferenceList.add(2);
        Preference preference = new Preference(preferenceList);
        mealTypeSection = new MealTypeSection("1", foodItem, "Breakfast", preference);
        diningSection = new DiningStation("Stocks", foodItem, preference);
    }

    @NonNull
    private static JSONObject getJsonObject() throws JSONException {
        String valueStr = "{\"id\":\"27444491\",\"label\":\"oatmeal\",\"recipes\":null,\"description\":\"\",\"short_name\":\"\",\"raw_cooked\":\"0\",\"is_rotating\":\"0\",\"zero_entree\":\"0\",\"cor_icon\":{\"4\":\"Vegan\"},\"ordered_cor_icon\":{\"0002-0004\":{\"id\":\"4\",\"label\":\"Vegan\"}},\"nextepid\":null,\"price\":\"\",\"sizes\":[],\"nutrition\":{\"kcal\":\"\",\"well_being\":\"\",\"well_being_image\":\"\"},\"special\":1,\"tier3\":0,\"tier\":1,\"rating\":\"\",\"connector\":\"\",\"options\":[],\"station_id\":\"13896\",\"station\":\"<strong>@Stocks<\\/strong>\",\"nutrition_details\":{},\"ingredients\":\"\",\"nutrition_link\":\"\",\"sub_station_id\":\"\",\"sub_station\":\"\",\"sub_station_order\":\"\",\"monotony\":{}}";
        JSONObject value = new JSONObject(valueStr);
        return value;
    }

    /**
     * Tests the dining sections list returned by the getDiningSections method in MealTypeSection class.
     * Note that setDiningSectionHashMap and getMealTypeSectionName methods will have to be working
     * properly for this method to work
     * @throws JSONException
     */
    @Test
    public void getDiningSections() throws JSONException {
        mealTypeSection.setDiningSectionHashMap();
        mealTypeSection.getMealTypeSection();
        List<DiningStation> diningSections = mealTypeSection.getDiningSections();
        diningSection.getDiningSection();
        assertEquals(diningSection.toString(), diningSections.get(0).toString());
    }

    /**
     * Tests the name of the mealTypeSection returned by the getMealTypeSectionName in MealTypeSection
     * class
     */
    @Test
    public void getMealTypeSectionName() {
        String mealTypeSectionName = mealTypeSection.getMealTypeSectionName();
        assertEquals("Breakfast Specials", mealTypeSectionName);
    }
}