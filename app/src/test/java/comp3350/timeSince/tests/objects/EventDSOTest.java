package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.objects.EventDSO;

public class EventDSOTest {

    public static EventDSO event;
    public static String name;
    public static Calendar date;
    public static Calendar targetDate;

    @Before
    public void instantiateObject() {
        String message = "The event should not be null";
        name = "event0";
        date = Calendar.getInstance();
        event = new EventDSO(1, date, name);
        assertNotNull(message, event);
        targetDate = DateUtils.timestampToCal(null);
    }

    @Test
    public void getName() {
        String message = String.format("The event name should be %s",
                name);
        assertEquals(message, name, event.getName());
    }

    @Test
    public void getDateCreated() {
        String message = "The event's date_created should not be null";
        assertNotNull(message, event.getDateCreated());
    }

    @Test
    public void getDescription() {
        String message = String.format("The event's description should be %s",
                "");
        assertEquals(message, event.getDescription(), "");
    }

    @Test
    public void setDescription() {
        String message;
        String description1 = "Good event!";
        String description2 = " ";

        event.setDescription(description1);
        message = String.format("The event's description should not be %s",
                description1);
        assertEquals(message, event.getDescription(), description1);
        event.setDescription(description2);
        message = String.format("The event's description should not be %s",
                description2);
        assertEquals(message, event.getDescription(), description2);
    }

    @Test
    public void appendDescription() {
        String message, newDescription;

        String currentDescription = event.getDescription();
        event.appendDescription("");
        message = String.format("The event's description should be %s",
                currentDescription);
        assertEquals(message, event.getDescription(), currentDescription);
        newDescription = "! ";
        event.appendDescription(newDescription);
        message = String.format("The event's description should not be %s",
                currentDescription + newDescription);
        assertEquals(message, event.getDescription(), currentDescription + newDescription);
    }

    @Test
    public void setFavorite() {
        event.setFavorite(true);
        assertTrue("The event should be a favorite", event.isFavorite());

        event.setFavorite(false);
        assertFalse("The event should not be a favorite", event.isFavorite());
    }

    @Test
    public void testSetName() {
        String newName = "Water Plants";
        event.setName(newName);
        String message = String.format("The event name should be %s",
                newName);
        assertEquals(message, newName, event.getName());
    }

    @Test
    public void testSetTargetFinishTime() {
        Calendar newDate = DateUtils.timestampToCal(null);
        event.setTargetFinishTime(newDate);
        String message = String.format("The event target finish time should be %s",
                newDate);
        assertEquals(message, newDate, event.getTargetFinishTime());
    }

    @Test
    public void testSetFrequency() {
        int twoWeeks = 20160; // two weeks in minutes
        String message = String.format("The event frequency should be %d", twoWeeks);
        event.setFrequency(twoWeeks);
        assertEquals(message, twoWeeks, event.getFrequency());

        int negNum = -10;
        event.setFrequency(negNum);
        assertNotEquals("The event frequency should not be a negative number",
                negNum, event.getFrequency());
        assertEquals("The event frequency should not have changed",
                twoWeeks, event.getFrequency());
    }

    @Test
    public void testEquals() {
        EventDSO other = new EventDSO(1, date, "Water Plants");
        assertTrue("Events with the same ID should be equal",
                event.equals(other));
        other = new EventDSO(2, date, "Water Plants");
        assertFalse("Events with different ID's should not be equal",
                event.equals(other));
    }
}
