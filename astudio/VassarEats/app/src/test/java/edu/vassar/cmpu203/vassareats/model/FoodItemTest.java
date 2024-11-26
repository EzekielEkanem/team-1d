package edu.vassar.cmpu203.vassareats.model;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

public class FoodItemTest {
    /**
     * Tests the diet label returned by the getDietLabels method in FoodItem class
     */

    @Test
    public void getDietLabels() throws JSONException {
//        FoodItem foodItem1 = new FoodItem("", new JSONObject(""));
//        HashSet<String> dietLabels1 = foodItem1.getDietLabels();
//        assertEquals(0, dietLabels1.size());
//
//        JSONParser parser = new JSONParser();
//        try {
//            Object obj = parser.parse(new FileReader("/model/course.json"));
//            JSONObject value = new JSONObject();
//            FoodItem foodItem2 = new FoodItem("27444487", value);
//            HashSet<String> dietLabels2 = foodItem2.getDietLabels();
//            assertEquals(1, dietLabels2);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void testToString() {
    }
}
