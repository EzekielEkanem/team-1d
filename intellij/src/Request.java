package intellij.src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Request {
    String urlStr;
    String html;

    public Request(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getWebPage(){

        String html = null; // return value

        try {
            // create URL, issue request
            URL url = new URI(urlStr).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            final int status = con.getResponseCode();
            if (status != 200) { // 200 means success
                System.err.printf("Error: code %d", status);
                return html;
            }

            // read and save html
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();

            html = content.toString();

        } catch(URISyntaxException | IOException e) {
            System.err.printf("Error: exception %s", e.getMessage());
        }

        this.html = html;
        return html;
    }

    public JSONObject getJsonMenu() throws ParseException {
        // Isolate JSON String from HTML
        String pattern = "Bamco.menu_items = (.*);";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(html);
        matcher.find();

        // Remove final semicolon from JSON String
        String jsonText = matcher.group(1).split(";")[0];

        // Parse the jsonText string into a JSON object
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonText);
    }
}

