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

public class EventManagerTest {

    private EventManager eventManager;
    private IUserPersistence userPersistence;
    private IEventPersistence eventPersistence;
    private IEventLabelPersistence eventLabelPersistence;
    private EventDSO event1, event2, event3;
    private Calendar currDate;
    private int id1 = 100;
    private int id2 = 101;
    private int id3 = 102;

    @Before
    public void setUp() {
        userPersistence = mock(UserPersistenceHSQLDB.class);
        eventPersistence = mock(EventPersistenceHSQLDB.class);
        eventLabelPersistence = mock(EventLabelPersistenceHSQLDB.class);
        eventManager = new EventManager(userPersistence, eventPersistence,
                eventLabelPersistence);
        currDate = Calendar.getInstance();
        event1 = new EventDSO(id1, currDate, "evnet1");
        event2 = new EventDSO(id2, currDate, "event2");
        event3 = new EventDSO(id3, currDate, "event3");
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByID() {
        when(eventPersistence.getEventByID(id1)).thenReturn(event1);
        when(eventPersistence.getEventByID(id2)).thenReturn(event2);
        when(eventPersistence.getEventByID(id3)).thenReturn(event3);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        assertEquals("eventManager.getEventByID(0) should return event1",
                event1, eventManager.getEventByID(id1));
        assertEquals("eventManager.getEventByID(1) should return event2",
                event2, eventManager.getEventByID(id2));
        assertEquals("eventManager.getEventByID(2) should return event3",
                event3, eventManager.getEventByID(id3));

        verify(eventPersistence).getEventByID(id1);
        verify(eventPersistence).getEventByID(id2);
        verify(eventPersistence).getEventByID(id3);

        assertNull("eventManager.getEventByID(-1) should return null",
                eventManager.getEventByID(-1));
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEvent() {
        when(eventPersistence.getEventByID(id1))
                .thenReturn(event1);
        when(eventPersistence.getEventByID(-1))
                .thenThrow(EventNotFoundException.class);
        when(eventPersistence.updateEvent(any(EventDSO.class)))
                .thenReturn(event1);

        assertEquals("eventManager.updateEventName() should return event1",
                event1, eventManager.updateEventName("updatedEventName", id1));
        assertEquals("eventManager.updateEventDescription() should return event1",
                event1, eventManager.updateEventDescription("updatedEventDesc", id1));
        assertEquals("eventManager.updateEventFinishTime() should return event1",
                event1, eventManager.updateEventFinishTime(currDate, id1));
        assertEquals("eventManager.updateEventFavorite() should return event1",
                event1, eventManager.updateEventFavorite(true, id1));

        verify(eventPersistence, times(4))
                .getEventByID(id1);
        verify(eventPersistence, times(4))
                .updateEvent(any(EventDSO.class));

        eventManager.updateEventName("updateEventName", -1); // should throw exception
        eventManager.updateEventDescription("updateEventDesc", -1); // should throw exception
        eventManager.updateEventFinishTime(currDate, -1); // should throw exception
        eventManager.updateEventFavorite(true, -1); // should throw exception
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEvent() {
        when(eventPersistence.getEventByID(id1)).thenReturn(event1);
        when(eventPersistence.getEventByID(id2)).thenReturn(event2);
        when(eventPersistence.getEventByID(id3)).thenReturn(event3);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        when(eventPersistence.deleteEvent(any(EventDSO.class)))
                .thenReturn(event1).thenReturn(event2).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1",
                event1, eventManager.deleteEvent(id1));
        assertEquals("eventManager.deleteEvent(event2) should return event2",
                event2, eventManager.deleteEvent(id2));
        assertEquals("eventManager.deleteEvent(event3) should return event3",
                event3, eventManager.deleteEvent(id3));

        verify(eventPersistence, times(3))
                .deleteEvent(any(EventDSO.class));

        assertEquals("eventManager.deleteEvent() of none existent event should throw exception",
                event3, eventManager.deleteEvent(-1));
    }

    @Test(expected = EventNotFoundException.class)
    public void testMarkEventAsDone() {
        when(eventPersistence.getEventByID(id1)).thenReturn(event1);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        eventManager.markEventAsDone(id1, true);
        assertTrue("event1 should be marked as done", event1.isDone());

        eventManager.markEventAsDone(id1, false);
        assertFalse("event1 should be marked as not done", event1.isDone());

        verify(eventPersistence, times(2)).getEventByID(id1);

        eventManager.markEventAsDone(-1, false); //should throw exception
    }

    @Test(expected = EventNotFoundException.class)
    public void testIsDone() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(9999, 10, 10);

        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);
        when(eventPersistence.getEventByID(id1)).thenReturn(event1);
        when(eventPersistence.getEventByID(id2)).thenReturn(event2);

        event1.setTargetFinishTime(Calendar.getInstance());
        event2.setTargetFinishTime(futureDate);

        assertTrue("event with id 0 should be done",
                eventManager.isDone(id1));
        assertFalse("event with id 1 should not be done",
                eventManager.isDone(id2));

        verify(eventPersistence).getEventByID(id1);
        verify(eventPersistence).getEventByID(id2);

        eventManager.isDone(-1); // should throw exception
    }

    @Test(expected = Exception.class)
    public void testInsertEvent() {
        UserDSO user = new UserDSO("user1", currDate, "hash1");
        EventLabelDSO eventLabel = new EventLabelDSO(id1, "eventLabel1");
        String eventName = "event1", tagName = "Sports";

        when(userPersistence.getUserByID("userNotFound"))
                .thenThrow(UserNotFoundException.class);
        when(userPersistence.getUserByID("user1"))
                .thenReturn(user);
        when(eventPersistence.insertEvent(any(EventDSO.class)))
                .thenReturn(event1)
                .thenReturn(event1)
                .thenThrow(DuplicateEventException.class);
        when(eventLabelPersistence.insertEventLabel(any(EventLabelDSO.class)))
                .thenReturn(eventLabel);

        Assert.assertNotNull("eventManager.insertEvent(event1) should return event1",
                eventManager.insertEvent("user1", Calendar.getInstance(),
                        eventName, tagName, true));

        verify(eventPersistence).insertEvent(any(EventDSO.class));
        verify(eventLabelPersistence).insertEventLabel(any(EventLabelDSO.class));

        eventManager.insertEvent("userNotFound", Calendar.getInstance(),
                eventName, tagName, true); //should throw UserNotFoundException

        eventManager.insertEvent("user1", Calendar.getInstance(),
                eventName, tagName, true); // should throw duplicateEventException

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
