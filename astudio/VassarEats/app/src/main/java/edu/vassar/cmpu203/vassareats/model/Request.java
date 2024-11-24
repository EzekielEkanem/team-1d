package edu.vassar.cmpu203.vassareats.model;

import static java.lang.System.out;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private String urlStr;
    private String html;
    private IWebPageCallback callback;

    public Request(String urlStr, IWebPageCallback callback) {
        this.urlStr = urlStr;
        this.callback = callback;
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public void getWebPage() {
        executor.execute(() -> {
            String html = null;
            try {
                URL url = new URI(urlStr).toURL();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int status = con.getResponseCode();
                if (status >= 200 && status < 300) { // Success
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    html = content.toString();

                    // Post result to the main thread
                    String finalHtml = html;
                    handler.post(() -> callback.onSuccess(finalHtml));
                } else {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    StringBuilder errorContent = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorContent.append(line);
                    }
                    errorReader.close();
                    String errorMessage = "Error code: " + status + ", message: " + errorContent;
                    handler.post(() -> callback.onError(errorMessage));
                }
            } catch (Exception e) {
                String errorMessage = "Exception: " + e.getMessage();
                handler.post(() -> callback.onError(errorMessage));
            }
        });
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
            String pattern = String.format("Bamco.dayparts\\['%d'] = (.*);", index);
            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(html);
            matcher.find();

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
