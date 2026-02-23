package edu.vassar.cmpu203.vassareats.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class ParentItemTest {

    @Test
    public void testGettersAndExpansion() {
        List<Object> children = new ArrayList<>();
        children.add("Station A");
        children.add("Food X");

        ParentItem parent = new ParentItem("Breakfast", children);

        assertEquals("Breakfast", parent.getTitle());
        assertEquals(children, parent.getChildItems());
        assertFalse(parent.isExpanded());

        parent.setExpanded(true);
        assertTrue(parent.isExpanded());

        parent.setExpanded(false);
        assertFalse(parent.isExpanded());
    }
}

