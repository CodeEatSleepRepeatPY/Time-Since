package comp3350.timeSince.tests.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;

public class EventPersistenceTest {

    private IEventPersistence eventDatabase;
    private EventDSO event1, event2, event3;
    private List<EventDSO> eventList;

    @Before
    public void setUp() {
        eventDatabase = new EventPersistence();
        event1 = new EventDSO("event1");
        event2 = new EventDSO("event2");
        event3 = new EventDSO("event3");
        eventList = new ArrayList<>();
        eventList.add(event2);
        eventList.add(event3);
        eventList.add(event1);
    }

    @After
    public void tearDown() {
        eventDatabase.deleteEvent(event1);
        eventDatabase.deleteEvent(event2);
        eventDatabase.deleteEvent(event3);
        eventDatabase = null;
    }

    @Test
    public void testGetEventList() {
        assertNotNull("Newly created database object should not be null", eventDatabase);
        assertEquals("Newly created database should have no users", eventDatabase.numEvents(), 0);
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);
        List<EventDSO> actual = eventDatabase.getEventList();
        assertTrue("Database should contain event1", actual.contains(event1));
        assertTrue("Database should contain event2", actual.contains(event2));
        assertTrue("Database should contain event3", actual.contains(event3));
        assertTrue("Database should have all existing events", actual.containsAll(eventList));
        assertTrue("Database should have all existing events", eventList.containsAll(actual));
        assertFalse("Database should not contain an event that does not exist", actual.contains(new EventDSO("event4")));
    }

    @Test
    public void testGetEventByID() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        assertEquals("The correct event should be returned if present", event1, eventDatabase.getEventByID("event1"));
        assertNull("Null should be returned if event is not present", eventDatabase.getEventByID("event4"));
    }

    @Test
    public void testInsertEvent() {
        assertEquals("Size of database should be 0", eventDatabase.numEvents(), 0);
        assertNotNull(eventDatabase.insertEvent(event1));
        assertEquals("Size of database should be 1", 1, eventDatabase.numEvents());
        eventDatabase.insertEvent(event2);
        assertEquals("Size of database should be 2", 2, eventDatabase.numEvents());
        eventDatabase.insertEvent(event3);
        assertEquals("Size of database should be 3", 3, eventDatabase.numEvents());
        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be 3", 3, eventDatabase.numEvents());
        assertEquals("Database should contain event2", event2, eventDatabase.getEventByID("event2"));
        assertNull("Should not be able to insert a duplicate", eventDatabase.insertEvent(event1));
    }

    @Test
    public void testUpdateEvent() {
        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be 1", 1, eventDatabase.numEvents());
        event1.setDescription("hello");
        eventDatabase.updateEvent(event1);
        assertEquals("New attributes should match", "hello", eventDatabase.getEventByID("event1").getDescription());
        eventDatabase.insertEvent(event3);
    }

    @Test
    public void testDeleteEvent() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);
        assertEquals("Size of database should be 3", 3, eventDatabase.numEvents());
        eventDatabase.deleteEvent(event2);
        assertEquals("Size of database should be 2", 2, eventDatabase.numEvents());
        assertNull("Deleted event should no longer be in database", eventDatabase.getEventByID(event2.getName()));
        assertEquals("If event exists, return the event that was deleted", event1, eventDatabase.deleteEvent(event1));
        assertNull("Shouldn't be able to delete an event that doesn't exist", eventDatabase.deleteEvent(new EventDSO("event4")));
        assertEquals("Size of database should be 1", 1, eventDatabase.numEvents());
        eventDatabase.deleteEvent(event3);
        assertEquals("Size of database should be 0", 0, eventDatabase.numEvents());
        assertNull("Should return null when database is empty", eventDatabase.deleteEvent(event1));
    }
}