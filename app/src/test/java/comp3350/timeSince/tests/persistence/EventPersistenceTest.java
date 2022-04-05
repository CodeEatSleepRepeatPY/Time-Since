package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;

public class EventPersistenceTest {

    private IEventPersistence eventDatabase;
    private UserDSO user;
    private EventDSO event1, event2, event3, event4;
    private final Calendar date = Calendar.getInstance();
    private List<EventDSO> eventList;

    @Before
    public void setUp() {
        eventDatabase = new EventPersistence();
        user = new UserDSO("admin", date, "12345");
        event1 = new EventDSO(user.getID(), "event1", date);
        event2 = new EventDSO(user.getID(), "event2", date);
        event3 = new EventDSO(user.getID(),  "event3", date);
        event4 = new EventDSO(user.getID(), "event1", date); // for duplication checks
        eventList = new ArrayList<>(Arrays.asList(event1, event2, event3));
    }

    @After
    public void tearDown() {
        List<EventDSO> tempList = eventDatabase.getEventList();

        if (tempList.contains(event1)) {
            eventDatabase.deleteEvent(user, event1);
            event1 = null;
        }
        if (tempList.contains(event2)) {
            eventDatabase.deleteEvent(user, event2);
        }
        if (tempList.contains(event3)) {
            eventDatabase.deleteEvent(user, event3);
        }
    }

    @Test
    public void testGetEventList() {
        assertNotNull("Newly created database object should not be null",
                eventDatabase);
        assertEquals("Newly created database should have no users",
                0, eventDatabase.numEvents());

        eventDatabase.insertEvent(user, event1);
        eventDatabase.insertEvent(user, event2);
        eventDatabase.insertEvent(user, event3);
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
        eventDatabase.insertEvent(user, event1);
        eventDatabase.insertEvent(user, event2);
        assertEquals("The correct event should be returned if present",
                event1, eventDatabase.getEventByID(user.getID(), event1.getName()));
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByIDException() {
        eventDatabase.insertEvent(user, event1);
        eventDatabase.getEventByID(user.getID(), event3.getName());
    }

    @Test
    public void testInsertEvent() {
        assertEquals("Size of database should be 0", 0,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(user, event1);
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());

        assertEquals("Inserted event should return", event2,
                eventDatabase.insertEvent(user, event2));
        assertEquals("Size of database should be 2", 2,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(user, event3);
        assertEquals("Size of database should be 3", 3,
                eventDatabase.numEvents());

        assertEquals("Database should contain event2", event2,
                eventDatabase.getEventByID(user.getID(), event2.getName()));
    }

    @Test(expected = DuplicateEventException.class)
    public void testInsertEventException() {
        eventDatabase.insertEvent(user, event1);
        eventDatabase.insertEvent(user, event2);
        eventDatabase.insertEvent(user, event1);
        eventDatabase.insertEvent(user, new EventDSO(user.getID(), "event4", date));
    }

    @Test
    public void testUpdateEvent() {
        eventDatabase.insertEvent(user, event1);
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());
        event1.setDescription("hello");
        eventDatabase.updateEvent(user, event1);
        assertEquals("New attributes should match", "hello",
                eventDatabase.getEventByID(user.getID(), event1.getName()).getDescription());

        event1.setName("good-bye");
        assertEquals("Updated event should be returned", "good-bye",
                eventDatabase.updateEvent(user, event1).getName());
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventException() {
        eventDatabase.updateEvent(user, event1); // should not be able to update an event not in db
    }

    @Test
    public void testDeleteEvent() {
        eventDatabase.insertEvent(user, event1);
        eventDatabase.insertEvent(user, event2);
        eventDatabase.insertEvent(user, event3);

        assertEquals("Size of database should be 3", 3,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(user, event2);
        assertEquals("Size of database should be 2", 2,
                eventDatabase.numEvents());
        assertEquals("If event exists, return the event that was deleted", event1,
                eventDatabase.deleteEvent(user, event1));
        assertEquals("Size of database should be 1", 1,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(user, event3);
        assertEquals("Size of database should be 0", 0,
                eventDatabase.numEvents());
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventException() {
        eventDatabase.deleteEvent(user, event4); // should not be able to delete an event not in db
    }

}
