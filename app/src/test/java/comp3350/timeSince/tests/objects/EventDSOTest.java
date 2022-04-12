package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;

import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.objects.EventDSO;

@FixMethodOrder(MethodSorters.JVM)
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
        targetDate = Calendar.getInstance();
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

    @Test (expected = EventDescriptionException.class)
    public void testSetDescriptionException() {
        String tooLong = "The quick brown fox jumped over the lazy dog "
                + "The quick brown fox jumped over the lazy dog "
                + "The quick brown fox jumped over the lazy dog "
                + "The quick brown fox jumped over the lazy dog";
        event.setDescription(tooLong);
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

    @Test (expected = EventDescriptionException.class)
    public void testAppendDescriptionException() {
        String description = "The quick brown fox jumped over the lazy dog.";
        String append = "The quick brown fox jumped over the lazy dog "
                + "The quick brown fox jumped over the lazy dog "
                + "The quick brown fox jumped over the lazy dog";
        event.setDescription(description);
        event.appendDescription(append);
    }

    @Test
    public void testSetFavorite() {
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
        Calendar newDate = Calendar.getInstance();
        event.setTargetFinishTime(newDate);
        String message = String.format("The event target finish time should be %s",
                newDate);
        assertEquals(message, newDate, event.getTargetFinishTime());
    }

    @Test
    public void testIsDone() {
        assertFalse("Default should be not done.", event.isDone());
        event.setIsDone(true);
        assertTrue("After setting as done, Event should be done.",
                event.isDone());
        event.setIsDone(false);
        assertFalse("After setting the Event as not done, it should be not done.",
                event.isDone());
    }

    @Test
    public void testValidate() {
        assertTrue("An Event with valid ID and name should be valid.",
                event.validate());

        EventDSO badEvent = new EventDSO(-1,date,null);
        assertFalse("An Event with both invalid parameters should not be valid.",
                badEvent.validate());

        badEvent = new EventDSO(-1, date, "hello");
        assertFalse("An Event with an invalid ID should not be valid.",
                badEvent.validate());

        badEvent = new EventDSO(3, date, null);
        assertFalse("An Event with an invalid name should not be valid.",
                badEvent.validate());
    }

    @Test
    public void testToString() {
        String expected = String.format("Event Name: %s", event.getName());
        String message = "The Event should display as: 'EventID: %d, Name: ?id?, ?eventName?'";
        assertEquals(message, expected, event.toString());

        EventDSO newEvent = new EventDSO(1, date, null);
        assertEquals("The Event should display as: 'No Named Event' when no name is given.",
                "No Named Event", newEvent.toString());
    }

    @Test
    public void testEquals() {
        EventDSO other = new EventDSO(1, date, name);
        assertEquals("Events with the same ID and name should be equal",
                other, event);
        other = new EventDSO(1, date, "Water Plants");
        assertNotEquals("Events with the same ID but different names should not be equal",
                other, event);
        other = new EventDSO(2, date, "Clean Sink");
        assertNotEquals("Events with different ID's and different names should not be equal",
                other, event);
    }

}
