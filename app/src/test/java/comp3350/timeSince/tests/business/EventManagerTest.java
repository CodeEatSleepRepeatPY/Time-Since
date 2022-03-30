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

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import comp3350.timeSince.business.EventManager;
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

    @Before
    public void setUp() {
        userPersistence = mock(UserPersistenceHSQLDB.class);
        eventPersistence = mock(EventPersistenceHSQLDB.class);
        eventLabelPersistence = mock(EventLabelPersistenceHSQLDB.class);
        eventManager = new EventManager(userPersistence, eventPersistence,
                eventLabelPersistence);

        currDate = Calendar.getInstance();
        event1 = new EventDSO(0, currDate, "event1");
        event2 = new EventDSO(1, currDate, "event2");
        event3 = new EventDSO(2, currDate, "event3");
    }

    @Test
    public void testGetEventByID() {
        when(eventPersistence.getEventByID(0)).thenReturn(event1);
        when(eventPersistence.getEventByID(1)).thenReturn(event2);
        when(eventPersistence.getEventByID(2)).thenReturn(event3);

        assertEquals("eventManager.getEventByID(0) should return event1",
                event1, eventManager.getEventByID(0));
        assertEquals("eventManager.getEventByID(1) should return event2",
                event2, eventManager.getEventByID(1));
        assertEquals("eventManager.getEventByID(2) should return event3",
                event3, eventManager.getEventByID(2));
        assertNull("eventManager.getEventByID(-1) should return null",
                eventManager.getEventByID(-1));

        verify(eventPersistence).getEventByID(0);
        verify(eventPersistence).getEventByID(1);
        verify(eventPersistence).getEventByID(2);
    }

    @Test
    public void testInsertEvent() {
        when(eventPersistence.insertEvent(any(EventDSO.class)))
                .thenReturn(event1).thenReturn(event2).thenReturn(event3);

        assertEquals("eventManager.insertEvent(event1) should return event1",
                event1, eventManager.insertEvent("event1", currDate));
        assertEquals("eventManager.insertEvent(event2) should return event2",
                event2, eventManager.insertEvent("event2", currDate));
        assertEquals("eventManager.insertEvent(event3) should return event3",
                event3, eventManager.insertEvent("event3", currDate));

        verify(eventPersistence, times(3))
                .insertEvent(any(EventDSO.class));
    }

    @Test
    public void testUpdateEvent() {
        when(eventPersistence.getEventByID(0))
                .thenReturn(event1);
        when(eventPersistence.updateEvent(any(EventDSO.class)))
                .thenReturn(event1);

        assertEquals("eventManager.updateEventName() should return event1",
                event1, eventManager.updateEventName("updatedEventName", 0));
        assertEquals("eventManager.updateEventDescription() should return event1",
                event1, eventManager.updateEventDescription("updatedEventDesc", 0));
        assertEquals("eventManager.updateEventFinishTime() should return event1",
                event1, eventManager.updateEventFinishTime(currDate, 0));
        assertEquals("eventManager.updateEventFavorite() should return event1",
                event1, eventManager.updateEventFavorite(true, 0));

        verify(eventPersistence, times(4))
                .updateEvent(any(EventDSO.class));
    }

    @Test
    public void testDeleteEvent() {
        when(eventPersistence.getEventByID(0)).thenReturn(event1);
        when(eventPersistence.getEventByID(1)).thenReturn(event2);
        when(eventPersistence.getEventByID(2)).thenReturn(event3);

        when(eventPersistence.deleteEvent(any(EventDSO.class)))
                .thenReturn(event1).thenReturn(event2).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1",
                event1, eventManager.deleteEvent(0));
        assertEquals("eventManager.deleteEvent(event2) should return event2",
                event2, eventManager.deleteEvent(1));
        assertEquals("eventManager.deleteEvent(event3) should return event3",
                event3, eventManager.deleteEvent(2));

        verify(eventPersistence, times(3))
                .deleteEvent(any(EventDSO.class));
    }

    @Test
    public void testIsDone() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(9999, 10, 10);

        when(eventPersistence.getEventByID(0)).thenReturn(event1);
        when(eventPersistence.getEventByID(1)).thenReturn(event2);

        event1.setTargetFinishTime(Calendar.getInstance());
        event2.setTargetFinishTime(futureDate);

        assertTrue("event with id 0 should be done",
                eventManager.isDone(0));
        assertFalse("event with id 1 should not be done",
                eventManager.isDone(1));

        verify(eventPersistence).getEventByID(0);
        verify(eventPersistence).getEventByID(1);
    }

    @Test
    public void testCreateOwnEvent() {
        UserDSO user = new UserDSO("user1", currDate, "hash1");
        EventLabelDSO eventLabel = new EventLabelDSO(0, "eventLabel1");
        String eventName = "event1", tagName = "Sports";

        when(userPersistence.getUserByID("user1"))
                .thenReturn(user);
        when(eventPersistence.insertEvent(any(EventDSO.class)))
                .thenReturn(event1);
        when(eventLabelPersistence.insertEventLabel(any(EventLabelDSO.class)))
                .thenReturn(eventLabel);

        assertTrue("eventManager.deleteEvent(event1) should return event1",
                eventManager.createOwnEvent("user1", Calendar.getInstance(),
                        eventName, tagName, true));

        verify(userPersistence).getUserByID("user1");
        verify(eventPersistence).insertEvent(any(EventDSO.class));
        verify(eventLabelPersistence).insertEventLabel(any(EventLabelDSO.class));

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
    public void testNumEvents() {
        when(eventPersistence.numEvents()).thenReturn(5);
        assertEquals("eventManager.numEvents() should return 5",
                5, eventManager.numEvents());
        verify(eventPersistence).numEvents();
    }

    //TODO: testMarkEventAsDone
    //TODO: Test that the methods throw an exception

}
