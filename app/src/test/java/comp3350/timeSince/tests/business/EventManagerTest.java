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
    Date currDate;

    @Before
    public void setUp() {
        eventManager = mock(EventManager.class);

        currDate = new Date(System.currentTimeMillis());
        event1 = new EventDSO(0, currDate,"event1");
        event2 = new EventDSO(1, currDate,"event2");
        event3 = new EventDSO(2, currDate,"event3");
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
        when(eventManager.insertEvent("event1")).thenReturn(event1);
        when(eventManager.insertEvent("event2")).thenReturn(event2);
        when(eventManager.insertEvent("event3")).thenReturn(event3);

        assertEquals("eventManager.insertEvent(event1) should return event1", event1, eventManager.insertEvent("event1"));
        assertEquals("eventManager.insertEvent(event2) should return event2", event2, eventManager.insertEvent("event2"));
        assertEquals("eventManager.insertEvent(event3) should return event3", event3, eventManager.insertEvent("event3"));
        assertNull("eventManager.insertEvent(null) should return null", eventManager.insertEvent(null));

        verify(eventManager).insertEvent("event1");
        verify(eventManager).insertEvent("event2");
        verify(eventManager).insertEvent("event3");
    }


    @Test
    public void testUpdateEvent() {
        when(eventManager.updateEventName("updatedEventName", 0)).thenReturn(event1);
        when(eventManager.updateEventDescription("updatedEventDesc", 0)).thenReturn(event1);
        when(eventManager.updateEventFinishTime(currDate, 0)).thenReturn(event1);
        when(eventManager.updateEventFrequency(5, 0)).thenReturn(event1);
        when(eventManager.updateEventFavorite(true, 0)).thenReturn(event1);

        assertEquals("eventManager.updateEventName() should return event1", event1, eventManager.updateEventName("updatedEventName", 0));
        assertEquals("eventManager.updateEventDescription() should return event1", event1, eventManager.updateEventDescription("updatedEventDesc", 0));
        assertEquals("eventManager.updateEventFinishTime() should return event1", event1, eventManager.updateEventFinishTime(currDate, 0));
        assertEquals("eventManager.updateEventFrequency() should return event1", event1, eventManager.updateEventFrequency(5, 0));
        assertEquals("eventManager.updateEventFavorite() should return event1", event1, eventManager.updateEventFavorite(true, 0));
        assertNull("eventManager.updateEventName() should return null", eventManager.updateEventName("wow", -1));

        verify(eventManager).updateEventName("updatedEventName", 0);
        verify(eventManager).updateEventDescription("updatedEventDesc", 0);
        verify(eventManager).updateEventFinishTime(currDate, 0);
        verify(eventManager).updateEventFrequency(5, 0);
        verify(eventManager).updateEventFavorite(true, 0);

    }


    @Test
    public void testDeleteEvent() {
        when(eventManager.deleteEvent(0)).thenReturn(event1);
        when(eventManager.deleteEvent(1)).thenReturn(event2);
        when(eventManager.deleteEvent(2)).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1", event1, eventManager.deleteEvent(0));
        assertEquals("eventManager.deleteEvent(event2) should return event2", event2, eventManager.deleteEvent(1));
        assertEquals("eventManager.deleteEvent(event3) should return event3", event3, eventManager.deleteEvent(2));
        assertNull("eventManager.deleteEvent(null) should return null", eventManager.deleteEvent(-1));

        verify(eventManager).deleteEvent(0);
        verify(eventManager).deleteEvent(1);
        verify(eventManager).deleteEvent(2);
    }

    @Test
    public void testIsDone(){
        when(eventManager.isDone(0)).thenReturn(true);
        when(eventManager.isDone(1)).thenReturn(false);

        assertTrue("event with id 0 should be done", eventManager.isDone(0));
        assertFalse("event with id 1 should not be done", eventManager.isDone(1));

        verify(eventManager).isDone(0);
        verify(eventManager).isDone(1);
    }

    @Test
    public void testCreateOwnEvent(){
        UserDSO user = new UserDSO("user1", currDate, "hash1");
        Date dueDate = new Date(System.currentTimeMillis());
        String eventName = "event1", tagName = "Sports";

        eventManager.createOwnEvent("user1", dueDate, eventName, tagName, true);
        verify(eventManager).createOwnEvent("user1", dueDate, eventName, tagName, true);

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
