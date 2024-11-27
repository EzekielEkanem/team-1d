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

public class MealTypeTest {
    MealTypeSection mealTypeSection;
    MealType mealType;

    /**
     * Default constructor for test class MealTypeTest
     */
    public MealTypeTest() throws JSONException {
        List<JSONObject> jsonObjectList = getJsonObject();
        JSONObject mealDayPart = jsonObjectList.get(0);
        JSONObject value = jsonObjectList.get(1);
        ArrayList<Integer> preferenceList = new ArrayList<Integer>();
        preferenceList.add(2);
        Preference preference = new Preference(preferenceList);
        mealType = new MealType(1, mealDayPart, value, preference);

        HashSet<HashMap<String, JSONObject>> foodItem = new HashSet<HashMap<String, JSONObject>>();
        HashMap<String, JSONObject> foodItemHashMap = new HashMap<String, JSONObject>();
        foodItemHashMap.put("27444491", jsonObjectList.get(2));
        foodItem.add(foodItemHashMap);
        mealTypeSection = new MealTypeSection("1", foodItem, "Breakfast", preference);
    }

    @NonNull
    private static List<JSONObject> getJsonObject() throws JSONException {
        String mealDayPartStr = "{\"starttime\":\"07:00\",\"endtime\":\"11:30\",\"id\":\"1\",\"label\":\"Breakfast\",\"abbreviation\":\"B\",\"message\":\"Gordon Commons is an all-you-care-to-eat dine in only caf\\u00e9 \",\"stations\":[{\"order_id\":\"0001-13896\",\"id\":\"13896\",\"label\":\"Stocks\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[\"27444491\"]},{\"order_id\":\"0002-14781\",\"id\":\"14781\",\"label\":\"The Farmer's Table\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]},{\"order_id\":\"0003-15779\",\"id\":\"15779\",\"label\":\"Root\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]},{\"order_id\":\"0004-13897\",\"id\":\"13897\",\"label\":\"Home\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]},{\"order_id\":\"0012-15010\",\"id\":\"15010\",\"label\":\"Coffee & Sweets\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]},{\"order_id\":\"0014-15684\",\"id\":\"15684\",\"label\":\"Beverages\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]},{\"order_id\":\"0015-15009\",\"id\":\"15009\",\"label\":\"Pressed\",\"price\":\"\",\"note\":\"\",\"soup\":0,\"image\":\"\",\"items\":[]}],\"print_urls\":{\"day\":\"https:\\/\\/legacy.cafebonappetit.com\\/print-menu\\/cafe\\/1626\\/menu\\/515922\\/days\\/today\\/pgbrks\\/0\\/\",\"week\":\"https:\\/\\/legacy.cafebonappetit.com\\/weekly-menu\\/515922\"},\"starttime_formatted\":\"7:00 am\",\"endtime_formatted\":\"11:30 am\",\"time_formatted\":\"7:00 am - 11:30 am\"}";
        JSONObject mealDayPart = new JSONObject(mealDayPartStr);

        String valueStr = "{\"27444491\":{\"id\":\"27444491\",\"label\":\"oatmeal\",\"recipes\":null,\"description\":\"\",\"short_name\":\"\",\"raw_cooked\":\"0\",\"is_rotating\":\"0\",\"zero_entree\":\"0\",\"cor_icon\":{\"4\":\"Vegan\"},\"ordered_cor_icon\":{\"0002-0004\":{\"id\":\"4\",\"label\":\"Vegan\"}},\"nextepid\":null,\"price\":\"\",\"sizes\":[],\"nutrition\":{\"kcal\":\"\",\"well_being\":\"\",\"well_being_image\":\"\"},\"special\":1,\"tier3\":0,\"tier\":1,\"rating\":\"\",\"connector\":\"\",\"options\":[],\"station_id\":\"13896\",\"station\":\"<strong>@Stocks<\\/strong>\",\"nutrition_details\":{},\"ingredients\":\"\",\"nutrition_link\":\"\",\"sub_station_id\":\"\",\"sub_station\":\"\",\"sub_station_order\":\"\",\"monotony\":{}}}";
        JSONObject value = new JSONObject(valueStr);

        String valueStr2 = "{\"id\":\"27444491\",\"label\":\"oatmeal\",\"recipes\":null,\"description\":\"\",\"short_name\":\"\",\"raw_cooked\":\"0\",\"is_rotating\":\"0\",\"zero_entree\":\"0\",\"cor_icon\":{\"4\":\"Vegan\"},\"ordered_cor_icon\":{\"0002-0004\":{\"id\":\"4\",\"label\":\"Vegan\"}},\"nextepid\":null,\"price\":\"\",\"sizes\":[],\"nutrition\":{\"kcal\":\"\",\"well_being\":\"\",\"well_being_image\":\"\"},\"special\":1,\"tier3\":0,\"tier\":1,\"rating\":\"\",\"connector\":\"\",\"options\":[],\"station_id\":\"13896\",\"station\":\"<strong>@Stocks<\\/strong>\",\"nutrition_details\":{},\"ingredients\":\"\",\"nutrition_link\":\"\",\"sub_station_id\":\"\",\"sub_station\":\"\",\"sub_station_order\":\"\",\"monotony\":{}}";
        JSONObject value2 = new JSONObject(valueStr2);

        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        jsonObjectList.add(mealDayPart);
        jsonObjectList.add(value);
        jsonObjectList.add(value2);
        return jsonObjectList;
    }

    /**
     * Tests the meal type sections list returned by the getMealTypeSections method in MealType class.
     * setMealTypeSection and getMealType methods will have to be working properly for this method
     * to work
     * @throws JSONException
     */
    @Test
    public void getMealTypeSections() throws JSONException {
        mealType.setMealTypeSection();
        mealType.getMealType();
        List<MealTypeSection> mealTypeSections = mealType.getMealTypeSections();

        mealTypeSection.setDiningSectionHashMap();
        mealTypeSection.getMealTypeSection();

        assertEquals(mealTypeSection.toString(), mealTypeSections.get(0).toString());
    }

    /**
     * Tests the name of mealType returned by the getMealTypeName
     */
    @Test
    public void getMealTypeName() {
        String mealTypeName = mealType.getMealTypeName();
        assertEquals("Breakfast", mealTypeName);
    }
}