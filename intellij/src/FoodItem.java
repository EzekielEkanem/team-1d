package intellij.src;

import org.json.simple.JSONObject;

import java.util.HashSet;

import static java.lang.System.out;

public class FoodItem {
    private String id;
    private String name;
    private HashSet<String> dietLabels;

    public FoodItem(String id, JSONObject value){
        this.id = id;
        this.name = (String) value.get("label");
        this.dietLabels = new HashSet<String>();

        // looping through the object to find the cor_icon dictionary
        for (Object valueKey : value.keySet()) {
            String valueKeyStr = (String) valueKey;

            if (valueKeyStr.equals("cor_icon")){
                // If the icons is not empty then
                try {
                    JSONObject icons = (JSONObject) value.get(valueKeyStr);

                    if (icons != null){
                        for (Object iconKey : icons.keySet()) {
                            String iconKeyStr = (String) iconKey;

                            String dietRest = (String) icons.get(iconKeyStr);

                            dietLabels.add(dietRest);
                        }
                    }
                } catch(Exception e) {
                    continue;
                }
            }
        }

//        if (value.get("cor_icon") instanceof JSONObject) {
//            JSONObject icons = (JSONObject) value.get("cor_icon");
//            for (Object key : icons.keySet()) {
//                dietLabels.add((String) icons.get(key));
//            }
//        }
    }

    public HashSet<String> getDietLabels(){
        return dietLabels;
    }

    public String toString(){
        return "Food id: " + this.id + "\n" + "Food name: " + this.name + "\n" + "Dietary labels: " + this.dietLabels;
    }
}

