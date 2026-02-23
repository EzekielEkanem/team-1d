package edu.vassar.cmpu203.vassareats.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class MealTimeTest {

    @Test
    public void testGetters() {
        MealTime mt = new MealTime("Lunch", "11:30 - 13:30", 42);

        assertEquals("Lunch", mt.getMealName());
        assertEquals("11:30 - 13:30", mt.getTimeRange());
        assertEquals(42, mt.getBackgroundDrawableId());
    }
}
