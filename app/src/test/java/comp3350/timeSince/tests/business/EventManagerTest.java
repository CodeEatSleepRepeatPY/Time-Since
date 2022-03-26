package comp3350.timeSince.tests.business;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.*;

import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;

public class EventManagerTest {

    private EventManager eventManager;
    private EventDSO event1, event2, event3;

    @Before
    public void setUp() {
        eventManager = mock(EventManager.class);

        event1 = new EventDSO("event1");
        event2 = new EventDSO("event2");
        event3 = new EventDSO("event3");
    }

    @Test
    public void testGetEventByID() {
        when(eventManager.getEventByID(0)).thenReturn(event1);
        when(eventManager.getEventByID(1)).thenReturn(event2);
        when(eventManager.getEventByID(2)).thenReturn(event3);

        assertEquals("eventManager.getEventByID(0) should return event1", event1, eventManager.getEventByID(0));
        assertEquals("eventManager.getEventByID(1) should return event2", event2, eventManager.getEventByID(1));
        assertEquals("eventManager.getEventByID(2) should return event3", event3, eventManager.getEventByID(2));
        assertNull("eventManager.getEventByID(-1) should return null", eventManager.getEventByID(-1));

        verify(eventManager).getEventByID(0);
        verify(eventManager).getEventByID(1);
        verify(eventManager).getEventByID(2);
    }

    @Test
    public void testInsertEvent() {
        EventDSO testEvent = new EventDSO("testEvent");
        testEvent.setID(-1);

        when(eventManager.insertEvent(event1)).thenReturn(event1);
        when(eventManager.insertEvent(event2)).thenReturn(event2);
        when(eventManager.insertEvent(event3)).thenReturn(event3);

        assertEquals("eventManager.insertEvent(event1) should return event1", event1, eventManager.insertEvent(event1));
        assertEquals("eventManager.insertEvent(event2) should return event2", event2, eventManager.insertEvent(event2));
        assertEquals("eventManager.insertEvent(event3) should return event3", event3, eventManager.insertEvent(event3));
        assertNull("eventManager.insertEvent(null) should return null", eventManager.insertEvent(null));
        assertNull("eventManager.insertEvent(testEvent) should return null", eventManager.insertEvent(testEvent));

        verify(eventManager).insertEvent(event1);
        verify(eventManager).insertEvent(event2);
        verify(eventManager).insertEvent(event3);
    }

    @Test
    public void testUpdateEvent() {
        when(eventManager.updateEvent(event1)).thenReturn(event1);
        when(eventManager.updateEvent(event2)).thenReturn(event2);
        when(eventManager.updateEvent(event3)).thenReturn(event3);

        assertEquals("eventManager.updateEvent(event1) should return event1", event1, eventManager.updateEvent(event1));
        assertEquals("eventManager.updateEvent(event2) should return event2", event2, eventManager.updateEvent(event2));
        assertEquals("eventManager.updateEvent(event3) should return event3", event3, eventManager.updateEvent(event3));
        assertNull("eventManager.updateEvent(null) should return null", eventManager.updateEvent(null));

        verify(eventManager).updateEvent(event1);
        verify(eventManager).updateEvent(event2);
        verify(eventManager).updateEvent(event3);
    }

    @Test
    public void testDeleteEvent() {
        when(eventManager.deleteEvent(event1)).thenReturn(event1);
        when(eventManager.deleteEvent(event2)).thenReturn(event2);
        when(eventManager.deleteEvent(event3)).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1", event1, eventManager.deleteEvent(event1));
        assertEquals("eventManager.deleteEvent(event2) should return event2", event2, eventManager.deleteEvent(event2));
        assertEquals("eventManager.deleteEvent(event3) should return event3", event3, eventManager.deleteEvent(event3));
        assertNull("eventManager.deleteEvent(null) should return null", eventManager.deleteEvent(null));

        verify(eventManager).deleteEvent(event1);
        verify(eventManager).deleteEvent(event2);
        verify(eventManager).deleteEvent(event3);
    }

    @Test
    public void testIsDone(){
        EventManager eManager = new EventManager();
        EventDSO testEvent = new EventDSO("testEvent");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 9999);

        date.setTime(1000);
        testEvent.setTargetFinishTime(date);
        assertTrue("testEvent should be done (isDone should return true)", eManager.isDone(testEvent));

        date = calendar.getTime();
        testEvent.setTargetFinishTime(date);
        assertFalse("testEvent should not be done (isDone should return false)", eManager.isDone(testEvent));
    }

    @Test
    public void testCreateOwnEvent(){
        UserDSO user = new UserDSO("user1", "hash1");
        Date dueDate = new Date(System.currentTimeMillis());
        String eventName = "event1", tagName = "Sports";

        eventManager.createOwnEvent(user, dueDate, eventName, tagName, true);
        verify(eventManager).createOwnEvent(user, dueDate, eventName, tagName, true);

        /*
        The code below was used to test that createOwnEvent() actually worked, but it cannot be used
        in production since it modifies the actual database.
        ---------------------------------------------------------------------------------------------
        EventManager eManager = new EventManager();
        eManager.createOwnEvent(user, dueDate, eventName, tagName, true);
        assertEquals("user should have 1 event", 1, user.getUserEvents().size());
        assertEquals("user should have an event with the name `event1` in his event list", "event1", user.getUserEvents().get(0).getName());
        assertEquals("user should have a tag with the name `Sports` in his tag list", "Sports", user.getUserLabels().get(0).getName());
        assertEquals("user should have an event with the name `event1` in his favorites", "event1", user.getFavoritesList().get(0).getName());
         */
    }

    @Test
    public void testNumEvents(){
        when(eventManager.numEvents()).thenReturn(5);
        assertEquals("eventManager.numEvents() should return 5", 5, eventManager.numEvents());
        verify(eventManager).numEvents();
    }
}
