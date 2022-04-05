package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.hsqldb.EventLabelPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.EventPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.UserPersistenceHSQLDB;

@Deprecated
public class EventManagerTest {

    private EventManager eventManager;
    private IUserPersistence userPersistence;
    private IEventPersistence eventPersistence;
    private IEventLabelPersistence eventLabelPersistence;
    private EventDSO event1, event2, event3;
    private UserDSO user;
    private String userID;
    private Calendar currDate;

    @Before
    public void setUp() {
        currDate = Calendar.getInstance();
        userID = "admin";
        user = new UserDSO(userID, currDate, "12345");

        userPersistence = mock(UserPersistenceHSQLDB.class);
        eventPersistence = mock(EventPersistenceHSQLDB.class);
        eventLabelPersistence = mock(EventLabelPersistenceHSQLDB.class);

        eventManager = new EventManager(userID, userPersistence,
                eventPersistence, eventLabelPersistence);

        event1 = new EventDSO(userID, "event1", currDate);
        event2 = new EventDSO(userID, "event2", currDate);
        event3 = new EventDSO(userID, "event3", currDate);
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByID() {
        when(eventPersistence.getEventByID(userID, "event1")).thenReturn(event1);
        when(eventPersistence.getEventByID(userID, "event2")).thenReturn(event2);
        when(eventPersistence.getEventByID(userID, "event3")).thenReturn(event3);
        when(eventPersistence.getEventByID(userID, "event4")).thenThrow(EventNotFoundException.class);

        assertEquals("eventManager.getEventByID(1) should return event1",
                event1, eventManager.getEventByID("event1"));
        assertEquals("eventManager.getEventByID(2) should return event2",
                event2, eventManager.getEventByID("event2"));
        assertEquals("eventManager.getEventByID(3) should return event3",
                event3, eventManager.getEventByID("event3"));

        verify(eventPersistence).getEventByID(userID, "event1");
        verify(eventPersistence).getEventByID(userID, "event2");
        verify(eventPersistence).getEventByID(userID, "event3");

        assertNull("eventManager.getEventByID(-1) should return null",
                eventManager.getEventByID("event4"));
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEvent() {
        when(eventPersistence.getEventByID(userID, "event1"))
                .thenReturn(event1);
        when(eventPersistence.getEventByID(userID, "event4"))
                .thenThrow(EventNotFoundException.class);
        when(eventPersistence.updateEvent(user, any(EventDSO.class)))
                .thenReturn(event1);

        assertEquals("eventManager.updateEventName() should return event1",
                event1, eventManager.updateEventName("updatedEventName", "event1"));
        assertEquals("eventManager.updateEventDescription() should return event1",
                event1, eventManager.updateEventDescription("updatedEventDesc", "event1"));
        assertEquals("eventManager.updateEventFinishTime() should return event1",
                event1, eventManager.updateEventFinishTime(currDate, "event1"));
        assertEquals("eventManager.updateEventFavorite() should return event1",
                event1, eventManager.updateEventFavorite(true, "event1"));

        verify(eventPersistence, times(4))
                .getEventByID(user.getID(), "event1");
        verify(eventPersistence, times(4))
                .updateEvent(user, any(EventDSO.class));

        eventManager.updateEventName("updateEventName", "event4"); // should throw exception
        eventManager.updateEventDescription("updateEventDesc", "event4"); // should throw exception
        eventManager.updateEventFinishTime(currDate, "event4"); // should throw exception
        eventManager.updateEventFavorite(true,  "event4"); // should throw exception
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEvent() {
        when(eventPersistence.getEventByID(userID, "event1")).thenReturn(event1);
        when(eventPersistence.getEventByID(userID, "event2")).thenReturn(event2);
        when(eventPersistence.getEventByID(userID, "event3")).thenReturn(event3);
        when(eventPersistence.getEventByID(userID, "event4")).thenThrow(EventNotFoundException.class);

        when(eventPersistence.deleteEvent(user, any(EventDSO.class)))
                .thenReturn(event1).thenReturn(event2).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1",
                event1, eventManager.deleteEvent("event1"));
        assertEquals("eventManager.deleteEvent(event2) should return event2",
                event2, eventManager.deleteEvent("event2"));
        assertEquals("eventManager.deleteEvent(event3) should return event3",
                event3, eventManager.deleteEvent("event3"));

        verify(eventPersistence, times(3))
                .deleteEvent(user, any(EventDSO.class));

        assertEquals("eventManager.deleteEvent() of none existent event should throw exception",
                event3, eventManager.deleteEvent("event4"));
    }

    @Test(expected = EventNotFoundException.class)
    public void testMarkEventAsDone() {
        when(eventPersistence.getEventByID(userID, "event1")).thenReturn(event1);
        when(eventPersistence.getEventByID(userID, "event4")).thenThrow(EventNotFoundException.class);

        eventManager.markEventAsDone("event1", true);
        assertTrue("event1 should be marked as done", event1.isDone());

        eventManager.markEventAsDone("event1", false);
        assertFalse("event1 should be marked as not done", event1.isDone());

        verify(eventPersistence, times(2)).getEventByID(userID, "event1");

        eventManager.markEventAsDone("event4", false); //should throw exception
    }

    @Test(expected = EventNotFoundException.class)
    public void testIsDone() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(9999, 10, 10);

        when(eventPersistence.getEventByID(userID, "event4")).thenThrow(EventNotFoundException.class);
        when(eventPersistence.getEventByID(userID, "event1")).thenReturn(event1);
        when(eventPersistence.getEventByID(userID, "event2")).thenReturn(event2);

        event1.setTargetFinishTime(Calendar.getInstance());
        event2.setTargetFinishTime(futureDate);

        assertTrue("event with id 1 should be done",
                eventManager.isDone("event1"));
        assertFalse("event with id 2 should not be done",
                eventManager.isDone("event2"));

        verify(eventPersistence).getEventByID(userID, "event1");
        verify(eventPersistence).getEventByID(userID, "event2");

        eventManager.isDone("event4"); // should throw exception
    }

    @Test(expected = Exception.class)
    public void testInsertEvent() {
        eventManager = new EventManager(userID, userPersistence,
                eventPersistence, eventLabelPersistence);
        UserDSO thisUser = new UserDSO("user1", currDate, "hash1");
        EventLabelDSO eventLabel = new EventLabelDSO(userID, "eventLabel1");
        String eventName = "event1", tagName = "Sports", eventDesc = "desc";

        when(userPersistence.getUserByID("userNotFound"))
                .thenThrow(UserNotFoundException.class);
        when(userPersistence.getUserByID("user1"))
                .thenReturn(thisUser);
        when(eventPersistence.insertEvent(thisUser,any(EventDSO.class)))
                .thenReturn(event1)
                .thenReturn(event1)
                .thenThrow(DuplicateEventException.class);
        when(eventLabelPersistence.insertEventLabel(thisUser, any(EventLabelDSO.class)))
                .thenReturn(eventLabel);

        Assert.assertNotNull("eventManager.insertEvent(event1) should return event1",
                eventManager.insertEvent(Calendar.getInstance(),
                        eventName, tagName, eventDesc, true));

        verify(eventPersistence).insertEvent(thisUser, any(EventDSO.class));
        verify(eventLabelPersistence).insertEventLabel(thisUser, any(EventLabelDSO.class));

        eventManager.insertEvent(Calendar.getInstance(), eventName,
                tagName, eventDesc, true); //should throw UserNotFoundException

        eventManager.insertEvent(Calendar.getInstance(),
                eventName, tagName, eventDesc, true); // should throw duplicateEventException

        verify(userPersistence, times(2)).getUserByID("user1");
        verify(userPersistence).getUserByID("userNotFound");
    }

    @Test
    public void testNumEvents() {
        when(eventPersistence.numEvents()).thenReturn(5);
        assertEquals("eventManager.numEvents() should return 5",
                5, eventManager.numEvents());
        verify(eventPersistence).numEvents();
    }
}

