package comp3350.timeSince.tests.business;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.objects.EventDSO;

public class EventManagerTest {

    private EventManager eventManager;
    private EventDSO event1, event2, event3;

    @Before
    public void setUp() {
        eventManager = new EventManager();

        event1 = new EventDSO("event1");
        event2 = new EventDSO("event2");
        event3 = new EventDSO("event3");
    }

    @Test
    public void testGetEventByID() {
        eventManager.insertEvent(event1);
        eventManager.insertEvent(event2);
        eventManager.insertEvent(event3);

        assertEquals("event1 should have id 0", event1, eventManager.getEventByID(0));
        assertEquals("event2 should have id 1", event2, eventManager.getEventByID(1));
        assertEquals("event3 should have id 2", event3, eventManager.getEventByID(2));
        assertNull("there should be no event with an id of 4", eventManager.getEventByID(3));
    }

    @Test
    public void testInsertEvent() {
        assertEquals("number of events inserted should be 0", 0, eventManager.numEvents());
        assertNotNull(eventManager.insertEvent(event1));
        assertEquals("number of events inserted should be 1", 1, eventManager.numEvents());
        eventManager.insertEvent(event2);
        assertEquals("number of events inserted should be 2", 2, eventManager.numEvents());
        eventManager.insertEvent(event3);
        assertEquals("number of events inserted should be 3", 3, eventManager.numEvents());
        eventManager.insertEvent(event1);
        assertEquals("number of events inserted should still be 3", 3, eventManager.numEvents());
        assertEquals("eventManager should have inserted event2", event2, eventManager.getEventByID(event2.getID()));
        assertNull("should not be able to insert a duplicate", eventManager.insertEvent(event1));
    }

    @Test
    public void testUpdateEvent() {
        eventManager.insertEvent(event1);
        assertEquals("number of events inserted should be 1", 1, eventManager.numEvents());
        event1.setDescription("hello");
        eventManager.updateEvent(event1);
        assertEquals("New attributes should match", "hello", eventManager.getEventByID(event1.getID()).getDescription());
        eventManager.insertEvent(event3);
    }

    @Test
    public void testDeleteEvent() {
        eventManager.insertEvent(event1);
        eventManager.insertEvent(event2);
        eventManager.insertEvent(event3);
        assertEquals("number of events inserted should be 3", 3, eventManager.numEvents());
        eventManager.deleteEvent(event2);
        assertEquals("after removing event2 there should only be 2 events in the database", 2, eventManager.numEvents());
        assertNull("deleted event should no longer be in database", eventManager.getEventByID(event2.getID()));
        assertEquals("if event exists, return the event that was deleted", event1, eventManager.deleteEvent(event1));
        assertNull("shouldn't be able to delete an event that doesn't exist", eventManager.deleteEvent(new EventDSO("event4")));
        assertEquals("after removing event1 and event2 there should only be 1 event in the database", 1, eventManager.numEvents());
        eventManager.deleteEvent(event3);
        assertEquals("after removing event1, event2 and event3 there should be no events in the database", 0, eventManager.numEvents());
        assertNull("should return null when database is empty", eventManager.deleteEvent(event1));
    }

    @Test
    public void testIsDone(){
        EventDSO testEvent = new EventDSO("testEvent");
        Date oldDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 9999);

        oldDate.setTime(1000);
        testEvent.setTargetFinishTime(oldDate);
        assertTrue("testEvent should be done (isDone should return true)", eventManager.isDone(testEvent));

        oldDate = calendar.getTime();
        testEvent.setTargetFinishTime(oldDate);
        assertFalse("testEvent should not be done (isDone should return false)", eventManager.isDone(testEvent));
    }

    @Test
    public void testCreateOwnEvent(){

    }
}
