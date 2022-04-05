package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public class EventDSOTest {

    private EventDSO event;
    private String name;
    private Calendar date;
    private Calendar targetDate;
    private UserDSO user;
    private EventLabelDSO label1, label2, label3, label4;

    @Before
    public void instantiateObject() {
        String message = "The event should not be null";
        name = "event0";
        date = Calendar.getInstance();
        user = new UserDSO("user1", date, "12345");
        event = new EventDSO(user.getID(), name, date);
        assertNotNull(message, event);
        targetDate = Calendar.getInstance();

        label1 = new EventLabelDSO(user.getID(), "Kitchen");
        label2 = new EventLabelDSO(user.getID(), "Bathroom");
        label3 = new EventLabelDSO(user.getID(), "Garage");
        label4 = new EventLabelDSO(user.getID(), "Bedroom");
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

        EventDSO badEvent = new EventDSO(null,null, date);
        assertFalse("An Event with both invalid parameters should not be valid.",
                badEvent.validate());

        badEvent = new EventDSO(null, "hello", date);
        assertFalse("An Event with an invalid ID should not be valid.",
                badEvent.validate());
    }

    @Test
    public void testToString() {
        String expected = String.format("Event Name: %s", event.getName());
        String message = "The Event should display as: 'EventID: %d, Name: ?id?, ?eventName?'";
        assertEquals(message, expected, event.toString());

        event.setName(null);
        assertEquals("The Event should display as: 'No Named Event' when no name is given.",
                "No Named Event", event.toString());
    }

    @Test
    public void testEquals() {
        EventDSO other = new EventDSO(user.getID(), name, date);
        assertTrue("Events with the same ID should be equal",
                event.equals(other));
        other = new EventDSO(user.getID(), "Water Plants", date);
        assertFalse("Events with different ID's should not be equal",
                event.equals(other));
    }

    @Test
    public void testGetEventLabels() {
        assertEquals("The event should have no labels to start",
                0, event.getEventLabels().size());

        event.addLabel(label1);
        event.addLabel(label2);
        event.addLabel(label3);
        event.addLabel(label4);

        List<EventLabelDSO> eventLabels = event.getEventLabels();
        assertEquals("The event should now have 4 labels",
                4, eventLabels.size());

        assertTrue("The event should contain label1",
                eventLabels.contains(label1));
        assertTrue("The event should contain label2",
                eventLabels.contains(label2));
        assertTrue("The event should contain label3",
                eventLabels.contains(label3));
        assertTrue("The event should contain label4",
                eventLabels.contains(label4));
    }

    @Test
    public void testAddDuplicateEventLabel() {
        assertEquals("The event should have no labels to start",
                0, event.getEventLabels().size());

        event.addLabel(label1);

        assertEquals("The event should now have 1 label",
                1, event.getEventLabels().size());

        event.addLabel(label1);
        assertEquals("Attempting to add a label already in the event should do nothing",
                1, event.getEventLabels().size());
    }

    @Test
    public void testRemoveEventLabel() {
        assertEquals("The event should have no labels to start",
                0, event.getEventLabels().size());

        event.addLabel(label1);
        event.addLabel(label2);
        event.addLabel(label3);
        event.addLabel(label4);

        assertEquals("The event should now have 4 labels",
                4, event.getEventLabels().size());

        event.removeLabel(label1);
        event.removeLabel(label3);
        assertEquals("The event should now have two labels",
                2, event.getEventLabels().size());
        assertFalse("The event should not contain removed labels",
                event.getEventLabels().contains(label1));
        assertFalse("The event should not contain removed labels",
                event.getEventLabels().contains(label3));

        event.removeLabel(label1);
        assertEquals("Removing a label not in the list should do nothing",
                2, event.getEventLabels().size());
        assertTrue("Removing a label not in the list should do nothing",
                event.getEventLabels().contains(label2));
        assertTrue("Removing a label not in the list should do nothing",
                event.getEventLabels().contains(label4));
    }

}
