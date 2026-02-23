package edu.vassar.cmpu203.vassareats.model;

import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class RequestTest {

    @Test
    public void testGetJsonMenuAndGetMealDayParts() throws Exception {
        // Prepare a minimal HTML snippet that matches the regexes in Request
        String html = ""
                + "Some header text\n"
                + "Bamco.menu_items = {"
                + "\"item1\":{"
                +     "\"label\":\"oatmeal\","
                +     "\"tier\":1,"
                +     "\"station\":\"@Stocks<something>\","
                +     "\"cor_icon\":{}"
                + "}"
                + "};\n"
                + "Other content\n"
                + "Bamco.dayparts['1'] = {"
                +     "\"time_formatted\":\"7:00 - 10:00\","
                +     "\"stations\":[{\"items\":[\"item1\"]}]"
                + "};\n"
                + "Footer";

        Request req = new Request();

        // Use reflection to inject the html so tests do not perform network IO
        Field htmlField = Request.class.getDeclaredField("html");
        htmlField.setAccessible(true);
        htmlField.set(req, html);

        // Verify getJsonMenu parses the item
        org.json.JSONObject menuJson = req.getJsonMenu();
        assertNotNull(menuJson);
        assertTrue(menuJson.has("item1"));
        JSONObject item1 = menuJson.getJSONObject("item1");
        assertEquals("oatmeal", item1.getString("label"));

        // Verify getMealDayParts parses daypart 1
        HashMap<Integer, JSONObject> parts = req.getMealDayParts();
        assertNotNull(parts);
        assertTrue(parts.containsKey(1));
        JSONObject part1 = parts.get(1);
        assertEquals("7:00 - 10:00", part1.getString("time_formatted"));
        assertTrue(part1.has("stations"));
    }
}

