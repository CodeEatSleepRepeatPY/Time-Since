package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.PersistenceException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;

public class EventPersistenceTest {

    private IEventPersistence eventDatabase;
    private EventDSO event1, event2, event3, event4;
    private final Date date = new Date(System.currentTimeMillis());
    private List<EventDSO> eventList;

    @Before
    public void setUp() {
        eventDatabase = new EventPersistence();
        event1 = new EventDSO(1, date, "event1");
        event2 = new EventDSO(2, date, "event2");
        event3 = new EventDSO(3, date, "event3");
        event4 = new EventDSO(2, date, "event4"); // for duplication checks
        eventList = new ArrayList<>(Arrays.asList(event1, event2, event3));
    }

    @After
    public void tearDown() {
        List<EventDSO> tempList = eventDatabase.getEventList();

        if (tempList.contains(event1)) {
            eventDatabase.deleteEvent(event1);
            event1 = null;
        }
        if (tempList.contains(event2)) {
            eventDatabase.deleteEvent(event2);
        }
        if (tempList.contains(event3)) {
            eventDatabase.deleteEvent(event3);
        }
    }

    @Test
    public void testGetEventList() {
        assertNotNull("Newly created database object should not be null",
                eventDatabase);
        assertEquals("Newly created database should have no users",
                0, eventDatabase.numEvents());

        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);
        List<EventDSO> actual = eventDatabase.getEventList();

        assertTrue("Database should contain event1", actual.contains(event1));
        assertTrue("Database should contain event2", actual.contains(event2));
        assertTrue("Database should contain event3", actual.contains(event3));
        assertTrue("Database should have all existing events",
                actual.containsAll(eventList));
        assertTrue("Database should have all existing events",
                eventList.containsAll(actual));
        assertFalse("Database should not contain an event that does not exist",
                actual.contains(event4));
    }

    @Test
    public void testGetEventByID() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        assertEquals("The correct event should be returned if present",
                event1, eventDatabase.getEventByID(event1.getID()));
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByIDException() {
        eventDatabase.insertEvent(event1);
        eventDatabase.getEventByID(event3.getID());
    }

    @Test
    public void testInsertEvent() {
        assertEquals("Size of database should be 0", 0,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());

        assertEquals("Inserted event should return", event2,
                eventDatabase.insertEvent(event2));
        assertEquals("Size of database should be 2", 2,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(event3);
        assertEquals("Size of database should be 3", 3,
                eventDatabase.numEvents());

        assertEquals("Database should contain event2", event2,
                eventDatabase.getEventByID(event2.getID()));
    }

    @Test(expected = PersistenceException.class)
    public void testInsertEventException() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(new EventDSO(2, date, "event4"));
    }

    @Test
    public void testUpdateEvent() {
        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());
        event1.setDescription("hello");
        eventDatabase.updateEvent(event1);
        assertEquals("New attributes should match", "hello",
                eventDatabase.getEventByID(event1.getID()).getDescription());
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventException() {
        eventDatabase.updateEvent(event1); // should not be able to update an event not in db
    }

    @Test
    public void testDeleteEvent() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);

        assertEquals("Size of database should be 3", 3,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(event2);
        assertEquals("Size of database should be 2", 2,
                eventDatabase.numEvents());
        assertEquals("If event exists, return the event that was deleted", event1,
                eventDatabase.deleteEvent(event1));
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(event3);
        assertEquals("Size of database should be 0", 0,
                eventDatabase.numEvents());
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventException() {
        eventDatabase.insertEvent(event2);
        eventDatabase.deleteEvent(event2);
        assertNull("Deleted event should no longer be in database",
                eventDatabase.getEventByID(event2.getID()));
        assertNull("Shouldn't be able to delete an event that doesn't exist",
                eventDatabase.deleteEvent(new EventDSO(4, date, "event4")));
    }
}
