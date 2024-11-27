package edu.vassar.cmpu203.vassareats.model;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DiningSectionTest {
    DiningSection diningSection;

    /**
     * Default constructor for test class DiningSectionTest
     */
    public DiningSectionTest() throws JSONException {
        HashSet<HashMap<String, JSONObject>> foodItem = new HashSet<HashMap<String, JSONObject>>();
        HashMap<String, JSONObject> foodItemHashMap = new HashMap<String, JSONObject>();
        JSONObject value = getJsonObject();
        foodItemHashMap.put("27444491", value);
        foodItem.add(foodItemHashMap);
        ArrayList<Integer> preferenceList = new ArrayList<Integer>();
        preferenceList.add(2);
        Preference preference = new Preference(preferenceList);
        diningSection = new DiningSection("Stocks", foodItem, preference);
    }

    @NonNull
    private static JSONObject getJsonObject() throws JSONException {
        String valueStr = "{\"id\":\"27444491\",\"label\":\"oatmeal\",\"recipes\":null,\"description\":\"\",\"short_name\":\"\",\"raw_cooked\":\"0\",\"is_rotating\":\"0\",\"zero_entree\":\"0\",\"cor_icon\":{\"4\":\"Vegan\"},\"ordered_cor_icon\":{\"0002-0004\":{\"id\":\"4\",\"label\":\"Vegan\"}},\"nextepid\":null,\"price\":\"\",\"sizes\":[],\"nutrition\":{\"kcal\":\"\",\"well_being\":\"\",\"well_being_image\":\"\"},\"special\":1,\"tier3\":0,\"tier\":1,\"rating\":\"\",\"connector\":\"\",\"options\":[],\"station_id\":\"13896\",\"station\":\"<strong>@Stocks<\\/strong>\",\"nutrition_details\":{},\"ingredients\":\"\",\"nutrition_link\":\"\",\"sub_station_id\":\"\",\"sub_station\":\"\",\"sub_station_order\":\"\",\"monotony\":{}}";
        JSONObject value = new JSONObject(valueStr);
        return value;
    }

    /**
     * Tests the foodItems list returned by the getFoodItems method in DiningSection class. Note that
     * getDiningSection will have to be working properly for this to work
     */
    @Test
    public void getFoodItems() throws JSONException {
        diningSection.getDiningSection();
        assertEquals("Food id: 27444491\nFood name: oatmeal\nDietary labels: [Vegan]", diningSection.getFoodItems().get(0).toString());
    }

    /**
     * Tests the station returned by the getDiningSectionName method in DiningSection class
     */
    @Test
    public void getDiningSectionName() {
        String diningSectionName = diningSection.getDiningSectionName();
        assertEquals("Stocks", diningSectionName);
    }
}