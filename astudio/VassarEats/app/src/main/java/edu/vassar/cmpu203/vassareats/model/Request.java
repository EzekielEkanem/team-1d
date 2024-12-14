package edu.vassar.cmpu203.vassareats.model;

import static java.lang.System.out;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private String html;
    private String baseURL = "https://vassar.cafebonappetit.com/cafe/gordon/";


    public Request() {}

    // The parameter should later be changed to date instead of url completely
    public List<MealType> getJavaMenu(LocalDate date) throws JSONException, ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = date.format(formatter);

        // Construct the full URL
        String fullURL = baseURL + formattedDate + "/";

        getWebPage(fullURL);
        JSONObject jsonMenuObject = getJsonMenu();

        HashMap<Integer, JSONObject> mealDayParts = getMealDayParts();
        ArrayList<MealType> mealTypes = new ArrayList<MealType>();

        HashMap<Integer, String> mealTypeNames = new HashMap<Integer, String>();
        mealTypeNames.put(1, "Breakfast");
        mealTypeNames.put(2, "Brunch");
        mealTypeNames.put(3, "Lunch");
        mealTypeNames.put(739, "Light Lunch");
        mealTypeNames.put(4, "Dinner");
        mealTypeNames.put(7, "Late Night");

        for (Object key : mealDayParts.keySet()) {
            // Loop for creating  new MealTypes

            int keyStr = (int) key;
            JSONObject value = (JSONObject) mealDayParts.get(keyStr);

            // The new MealType object is created here
            MealType newMealType = new MealType(mealTypeNames.get(keyStr));

            mealTypes.add(newMealType);

            HashMap<String, String> sectionNameHashMap = new HashMap<>();
            sectionNameHashMap.put("1", newMealType.getMealTypeName() + " Specials");
            sectionNameHashMap.put("2", "Additional " + newMealType.getMealTypeName() + " Favorites");
            sectionNameHashMap.put("3", newMealType.getMealTypeName() + " Condiments and Extras");

            HashMap<String, HashSet<HashMap<String, JSONObject>>> mealTypeSection = new HashMap<String, HashSet<HashMap<String, JSONObject>>>();

            if (value.get("stations") instanceof JSONArray stations) {
                for (int i = 0; i < stations.length(); i++) {
                    Object stationKey = stations.get(i);
                    JSONObject station = (JSONObject) stationKey;
                    JSONArray items = (JSONArray) station.get("items");

                    if (!(items.length() == 0)) {

                        for (int j = 0; j < items.length(); j++) {
                            Object item = items.get(j);
                            String itemName = (String) item;
                            JSONObject foodItem = (JSONObject) jsonMenuObject.get(itemName);
                            String tier = String.valueOf(foodItem.get("tier"));
                            HashMap<String, JSONObject> dishes = new HashMap<String, JSONObject>();
                            dishes.put(itemName, foodItem);

                            if (!mealTypeSection.containsKey(tier)) {
                                mealTypeSection.put(tier, new HashSet<HashMap<String, JSONObject>>());
                                mealTypeSection.get(tier).add(dishes);
                            } else {
                                mealTypeSection.get(tier).add(dishes);
                            }
                        }
                    }
                }
            }

            char[] tiers = {'1', '2', '3'};

            for (char tier : tiers) {
                // Loop for creating new MealTypeSections

                String tierStr = String.valueOf(tier);
                HashSet<HashMap<String, JSONObject>> foodItems = mealTypeSection.get(tierStr);
                HashMap<String, HashSet<HashMap<String, JSONObject>>> diningSectionHashMap = new HashMap<>();

                // The new MealTypeSection object is created here
                MealTypeSection newMealTypeSection = new MealTypeSection(sectionNameHashMap.get(tierStr));

                newMealType.addMealTypeSection(newMealTypeSection);

                if (foodItems != null) {

                    for (HashMap<String, JSONObject> someMap : foodItems) {
                        for (String keyName : someMap.keySet()) {
                            String station = (String) someMap.get(keyName).get("station");
                            String[] stationsplit = station.split("@");
                            String[] stationsplit2 = stationsplit[1].split("<");

                            if (!diningSectionHashMap.containsKey(stationsplit2[0])) {
                                diningSectionHashMap.put(stationsplit2[0], new HashSet<HashMap<String, JSONObject>>());
                                diningSectionHashMap.get(stationsplit2[0]).add(someMap);
                            } else {
                                diningSectionHashMap.get(stationsplit2[0]).add(someMap);
                            }
                        }
                    }

                }

                for (String stationName : diningSectionHashMap.keySet()) {
                    // Loop for creating new DiningSections

                    // The new DiningStation object is created here
                    DiningStation diningStation = new DiningStation(stationName);

                    newMealTypeSection.addDiningStation(diningStation);

                    for (HashMap<String, JSONObject> anotherMap : diningSectionHashMap.get(stationName)) {
                        for (String keyName : anotherMap.keySet()) {
                            // The new FoodItem object is created here

                            HashSet<String> dietLabels = new HashSet<String>();

                            if (anotherMap.get(keyName).get("cor_icon") instanceof JSONObject) {
                                JSONObject icons = (JSONObject) anotherMap.get(keyName).get("cor_icon");

                                for (Iterator<String> it = icons.keys(); it.hasNext();) {
                                    Object dietLabel = it.next();

                                    dietLabels.add((String) icons.get((String) dietLabel));
                                }
                            }

                            // The new FoodItem object is created here
                            FoodItem newFood = new FoodItem((String) anotherMap.get(keyName).get("label"), keyName, dietLabels);

                            diningStation.addFoodItem(newFood);
                        }
                    }
                }
            }
        }

        // return Java menu
        return mealTypes;
    }

//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    Handler handler = new Handler(Looper.getMainLooper());

    public void getWebPage(String urlStr){

        String html = null; // return value

        try {
            // create URL, issue request
            URL url = new URI(urlStr).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            out.println(con);

            final int status = con.getResponseCode();
            if (status != 200) { // 200 means success
                System.err.printf("Error: code %d", status);
            }

            // read and save html
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            html = content.toString();

        } catch(URISyntaxException | IOException e) {
            System.err.printf("Error: exception %s", e.getMessage());
        }

        this.html = html;
    }


    public JSONObject getJsonMenu() throws ParseException, JSONException {
        // Isolate JSON String from HTML
        String pattern = "Bamco.menu_items = (.*);";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(html);
        matcher.find();

        // Remove final semicolon from JSON String
        String jsonText = matcher.group(1).split(";")[0];

        // Parse the jsonText string into a JSON object
        JSONObject parser = new JSONObject(jsonText);
        return parser;
    }

    public HashMap<Integer, JSONObject> getMealDayParts() throws ParseException, JSONException {
        // Make a list that contain dayparts indices
        int[] daypartsIndices = {1, 2, 3, 739, 4, 7};

        // Make a hashmap that maps dayparts to its json object
        HashMap<Integer, JSONObject> mealDayParts = new HashMap<>();

        for (int index : daypartsIndices) {
            // Isolate JSON String from HTML
            String pattern = String.format("Bamco\\.dayparts\\['%d'\\]\\s*=\\s*(\\{.*?\\});", index);

            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(html);

            if (matcher.find()) {
                // Remove final semicolon from JSON String
                String jsonText = matcher.group(1).split(";")[0];

                // Parse the jsonText string into a JSON object
                JSONObject parser = new JSONObject(jsonText);
                mealDayParts.put(index, parser);
            }
        }
        return mealDayParts;
    }
}
