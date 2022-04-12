package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class EventPersistenceHSQLDBTest {

    private IEventPersistence eventPersistence;
    private IEventLabelPersistence eventLabelPersistence;
    private EventDSO event1, event2, event3, event4;
    private final Calendar date = Calendar.getInstance();
    private List<EventDSO> eventList;
    private static final int initialCount = 8;
    private static final int initialLabelCount = 9;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();
        eventPersistence = Services.getEventPersistence(true);
        eventLabelPersistence = Services.getEventLabelPersistence(true);

        event1 = new EventDSO(initialCount + 1, date, "event1");
        event2 = new EventDSO(initialCount + 2, date, "event2");
        event3 = new EventDSO(initialCount + 3, date, "event3");
        event4 = new EventDSO(initialCount + 2, date, "event4"); // for duplication checks
        eventList = new ArrayList<>(Arrays.asList(event1, event2, event3));
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetEventList() {
        assertNotNull("Newly created database object should not be null",
                eventPersistence);
        assertEquals("Newly created database should have " + initialCount + " events",
                initialCount, eventPersistence.numEvents());

        eventPersistence.insertEvent(event1);
        eventPersistence.insertEvent(event2);
        eventPersistence.insertEvent(event3);
        List<EventDSO> actual = eventPersistence.getEventList();

        assertTrue("Database should contain event1", actual.contains(event1));
        assertTrue("Database should contain event2", actual.contains(event2));
        assertTrue("Database should contain event3", actual.contains(event3));
        assertTrue("Database should have all existing events",
                actual.containsAll(eventList));
        assertFalse("Database should not contain an event that does not exist",
                actual.contains(event4));
    }

    @Test
    public void testGetEventByID() {
        eventPersistence.insertEvent(event1);
        eventPersistence.insertEvent(event2);
        assertEquals("The correct event should be returned if present",
                event1, eventPersistence.getEventByID(event1.getID()));
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByIDException() {
        eventPersistence.insertEvent(event1);
        eventPersistence.getEventByID(event3.getID());
    }

    @Test
    public void testInsertEvent() {
        assertEquals("Size of database should be " + initialCount, initialCount,
                eventPersistence.numEvents());

        eventPersistence.insertEvent(event1);
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                eventPersistence.numEvents());

        assertEquals("Inserted event should return", event2,
                eventPersistence.insertEvent(event2));
        assertEquals("Size of database should be " + (initialCount + 2), initialCount + 2,
                eventPersistence.numEvents());

        eventPersistence.insertEvent(event3);
        assertEquals("Size of database should be " + (initialCount + 3), initialCount + 3,
                eventPersistence.numEvents());

        assertEquals("Database should contain event2", event2,
                eventPersistence.getEventByID(event2.getID()));
    }

    @Test(expected = DuplicateEventException.class)
    public void testInsertEventException() {
        eventPersistence.insertEvent(event1);
        eventPersistence.insertEvent(event2);
        eventPersistence.insertEvent(event1);
        eventPersistence.insertEvent(new EventDSO(2, date, "event4"));
    }

    @Test
    public void testUpdateEvent() {
        eventPersistence.insertEvent(event1);
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                eventPersistence.numEvents());
        event1 = eventPersistence.updateEventDescription(event1, "hello");
        assertEquals("New attributes should match", "hello",
                eventPersistence.getEventByID(event1.getID()).getDescription());

        event1 = eventPersistence.updateEventName(event1, "good-bye");
        assertEquals("Updated event should be returned", "good-bye",
                event1.getName());
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventException() {
        eventPersistence.updateEventName(event1, "not present"); // should not be able to update an event not in db
    }

    @Test
    public void testDeleteEvent() {
        eventPersistence.insertEvent(event1);
        eventPersistence.insertEvent(event2);
        eventPersistence.insertEvent(event3);

        assertEquals("Size of database should be " + (initialCount + 3), initialCount + 3,
                eventPersistence.numEvents());
        eventPersistence.deleteEvent(event2);
        assertEquals("Size of database should be " + (initialCount + 2), initialCount + 2,
                eventPersistence.numEvents());
        assertEquals("If event exists, return the event that was deleted", event1,
                eventPersistence.deleteEvent(event1));
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                eventPersistence.numEvents());
        eventPersistence.deleteEvent(event3);
        assertEquals("Size of database should be " + initialCount, initialCount,
                eventPersistence.numEvents());
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventException() {
        eventPersistence.deleteEvent(event4); // should not be able to delete an event not in db
    }

    @Test
    public void testGetNextID() {
        assertEquals("The first ID should be " + (initialCount + 1),
                initialCount + 1, eventPersistence.getNextID());
        eventPersistence.insertEvent(event1);
        assertEquals("The ID of the first event inserted should be " + (initialCount + 1),
                initialCount + 1, event1.getID());

        assertEquals("The second ID should be " + (initialCount + 2),
                initialCount + 2, eventPersistence.getNextID());
        eventPersistence.insertEvent(event2);
        assertEquals("The ID of the second event inserted should be " + (initialCount + 2),
                initialCount + 2, event2.getID());

        eventPersistence.insertEvent(event3);
        try {
            eventPersistence.insertEvent(event3);
        } catch (DuplicateEventException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("The next ID after three events, with one duplicate attempt should be " + (initialCount + 4),
                initialCount + 4, eventPersistence.getNextID());

        eventPersistence.deleteEvent(event2);
        assertNotEquals("The next ID after a deletion should not be the deleted ID.",
                event2.getID(), eventPersistence.getNextID());
        assertEquals("The next ID should be 4", initialCount + 4, eventPersistence.getNextID());
    }

    @Test
    public void testEventWithLabels() {
        EventLabelDSO label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");

        eventLabelPersistence.insertEventLabel(label1);
        eventLabelPersistence.insertEventLabel(label2);

        event1 = eventPersistence.insertEvent(event1);
        event1 = eventPersistence.addLabel(event1, label1);
        event1 = eventPersistence.addLabel(event1, label2);
        EventDSO result = eventPersistence.getEventByID(event1.getID());
        List<EventLabelDSO> labels = result.getEventLabels();

        assertEquals("The event should have 2 labels in it", 2, labels.size());
        assertTrue("The event should contain label1", labels.contains(label1));
        assertTrue("The event should contain label2", labels.contains(label2));

        event1 = eventPersistence.removeLabel(event1, label1);
        labels = event1.getEventLabels();
        assertEquals("The event should now have 1 label", 1, labels.size());
        assertTrue("The event should contain label2", labels.contains(label2));
        assertFalse("The event should not contain the deleted label1", labels.contains(label1));
    }

}
