package intellij.src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.HttpURLConnection;

public class Request {
    String urlStr;

    public Request(String urlStr) {
        urlStr = urlStr;
    }

    public static String getWebPage(String urlStr){

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

        return html;
    }
}

