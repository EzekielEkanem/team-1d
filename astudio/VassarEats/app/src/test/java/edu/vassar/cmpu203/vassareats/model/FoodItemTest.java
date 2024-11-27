package edu.vassar.cmpu203.vassareats.model;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

public class FoodItemTest {
    FoodItem foodItem;

    /**
     * Default constructor for test class FoodItemTest
     */
    public FoodItemTest() throws JSONException {
        String valueStr = "{\"id\":\"27444491\",\"label\":\"oatmeal\",\"recipes\":null,\"description\":\"\",\"short_name\":\"\",\"raw_cooked\":\"0\",\"is_rotating\":\"0\",\"zero_entree\":\"0\",\"cor_icon\":{\"4\":\"Vegan\"},\"ordered_cor_icon\":{\"0002-0004\":{\"id\":\"4\",\"label\":\"Vegan\"}},\"nextepid\":null,\"price\":\"\",\"sizes\":[],\"nutrition\":{\"kcal\":\"\",\"well_being\":\"\",\"well_being_image\":\"\"},\"special\":1,\"tier3\":0,\"tier\":1,\"rating\":\"\",\"connector\":\"\",\"options\":[],\"station_id\":\"13896\",\"station\":\"<strong>@Stocks<\\/strong>\",\"nutrition_details\":{},\"ingredients\":\"\",\"nutrition_link\":\"\",\"sub_station_id\":\"\",\"sub_station\":\"\",\"sub_station_order\":\"\",\"monotony\":{}}";
        JSONObject value = new JSONObject(valueStr);
        foodItem = new FoodItem("27444491", value);
    }

    /**
     * Tests the diet label returned by the getDietLabels method in FoodItem class
     */
    @Test
    public void getDietLabels() {
        HashSet<String> dietLabels = foodItem.getDietLabels();
        assertEquals(1, dietLabels.size());
    }

    /**
     * Tests the food name returned by the getFoodItemName method in FoodItem class
     */
    @Test
    public void getFoodItemName() {
        String foodItemName = foodItem.getFoodItemName();
        assertEquals("oatmeal", foodItemName);
    }

    /**
     * Tests the string returned by the toString method in FoodItem class
     */
    @Test
    public void testToString() {
        String foodItemString = foodItem.toString();
        assertEquals("Food id: 27444491\nFood name: oatmeal\nDietary labels: [Vegan]", foodItemString);
    }
}
