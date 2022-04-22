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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
import comp3350.timeSince.tests.utils.InitialDatabaseState;
import comp3350.timeSince.persistence.hsqldb.EventLabelPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.EventPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.UserPersistenceHSQLDB;

@FixMethodOrder(MethodSorters.JVM)
public class EventManagerTest {

    private EventManager eventManager;
    private IUserPersistence userPersistence;
    private IEventPersistence eventPersistence;
    private IEventLabelPersistence eventLabelPersistence;
    private EventDSO event1, event2, event3;
    private UserDSO user;
    private Calendar currDate;
    private static final int initialCount = InitialDatabaseState.NUM_EVENTS;
    private static final int initialUserCount = InitialDatabaseState.NUM_USERS;
    private static final int initialLabelCount = InitialDatabaseState.NUM_LABELS;

    @Before
    public void setUp() {
        userPersistence = mock(UserPersistenceHSQLDB.class);
        eventPersistence = mock(EventPersistenceHSQLDB.class);
        eventLabelPersistence = mock(EventLabelPersistenceHSQLDB.class);
        eventManager = new EventManager(eventPersistence, eventLabelPersistence);

        currDate = Calendar.getInstance();
        event1 = new EventDSO(initialCount + 1, currDate, "event1");
        event2 = new EventDSO(initialCount + 2, currDate, "event2");
        event3 = new EventDSO(initialCount + 3, currDate, "event3");
        user = new UserDSO(initialUserCount + 1, "user1@gmail.com", currDate, "hash1");
    }

    @Test
    public void testGetEventByID() {
        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);
        when(eventPersistence.getEventByID(initialCount + 2)).thenReturn(event2);
        when(eventPersistence.getEventByID(initialCount + 3)).thenReturn(event3);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        assertEquals("eventManager.getEventByID(1) should return event1",
                event1, eventManager.getEventByID(initialCount + 1));
        assertEquals("eventManager.getEventByID(2) should return event2",
                event2, eventManager.getEventByID(initialCount + 2));
        assertEquals("eventManager.getEventByID(3) should return event3",
                event3, eventManager.getEventByID(initialCount + 3));

        verify(eventPersistence).getEventByID(initialCount + 1);
        verify(eventPersistence).getEventByID(initialCount + 2);
        verify(eventPersistence).getEventByID(initialCount + 3);

        assertNull("eventManager.getEventByID(-1) should return null",
                eventManager.getEventByID(-1));
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEvent() {
        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        when(eventPersistence.updateEventName(event1, "updatedEventName")).thenReturn(event1);
        when(eventPersistence.updateEventDescription(event1, "updatedEventDesc")).thenReturn(event1);
        when(eventPersistence.updateEventFinishTime(event1, currDate)).thenReturn(event1);

        assertEquals("eventManager.updateEventName() should return event1",
                event1, eventManager.updateEventName("updatedEventName", initialCount + 1));
        assertEquals("eventManager.updateEventDescription() should return event1",
                event1, eventManager.updateEventDescription("updatedEventDesc", initialCount + 1));
        assertEquals("eventManager.updateEventFinishTime() should return event1",
                event1, eventManager.updateEventFinishTime(currDate, initialCount + 1));

        verify(eventPersistence, times(3)).getEventByID(initialCount + 1);

        eventManager.updateEventName("updateEventName", -1); // should throw exception
        eventManager.updateEventDescription("updateEventDesc", -1); // should throw exception
        eventManager.updateEventFinishTime(currDate, -1); // should throw exception
    }

    @Test
    public void testAddLabelsToEvent() {
        EventLabelDSO label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");
        EventLabelDSO label3 = new EventLabelDSO(initialLabelCount + 3, "Label3");
        EventLabelDSO label4 = new EventLabelDSO(initialLabelCount + 4, "Label4");
        List<EventLabelDSO> labelsList = Arrays.asList(label1, label2, label3, label4);

        when(eventLabelPersistence.labelExists(any(EventLabelDSO.class))).thenReturn(true);
        when(eventPersistence.eventExists(event2)).thenReturn(true);
        EventDSO testEvent = event2;
        testEvent.addLabel(label1);
        when(eventPersistence.addLabel(event2, label1)).thenReturn(testEvent);
        testEvent.addLabel(label2);
        when(eventPersistence.addLabel(event2, label2)).thenReturn(testEvent);
        testEvent.addLabel(label3);
        when(eventPersistence.addLabel(event2, label3)).thenReturn(testEvent);
        testEvent.addLabel(label4);
        when(eventPersistence.addLabel(event2, label4)).thenReturn(testEvent);

        EventDSO result = eventManager.addLabelsToEvent(event2, labelsList);
        assertEquals("Event2 should get returned", event2, result);
        assertEquals("The event should have 4 labels", 4, result.getEventLabels().size());

        verify(eventPersistence, times(1)).addLabel(event2, label1);
        verify(eventPersistence, times(1)).addLabel(event2, label2);
        verify(eventPersistence, times(1)).addLabel(event2, label3);
        verify(eventPersistence, times(1)).addLabel(event2, label4);
    }

    @Test (expected = EventNotFoundException.class)
    public void testAddLabelToEvent() {
        EventLabelDSO label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");

        when(eventLabelPersistence.labelExists(label1)).thenReturn(false);
        when(eventLabelPersistence.labelExists(label2)).thenReturn(true);
        when(eventPersistence.eventExists(event1)).thenReturn(false).thenReturn(true).thenReturn(true);
        when(eventLabelPersistence.insertEventLabel(label1)).thenReturn(label2);

        eventManager.addLabelToEvent(event1, label1); // should throw exception

        when(eventPersistence.addLabel(event1, label1)).thenReturn(event1);
        EventDSO result = eventManager.addLabelToEvent(event1, label1);
        assertEquals("Event1 should be returned", event1, result);
        assertTrue("The event should contain label1", result.getEventLabels().contains(label1));

        when(eventPersistence.addLabel(event1, label2)).thenReturn(event1);
        result = eventManager.addLabelToEvent(event1, label2);
        assertEquals("Event1 should be returned", event1, result);
        assertTrue("The event should contain label1", result.getEventLabels().contains(label1));
        assertTrue("The event should contain label2", result.getEventLabels().contains(label2));

        verify(eventLabelPersistence, times(2)).labelExists(any(EventLabelDSO.class));
        verify(eventLabelPersistence, times(1)).insertEventLabel(any(EventLabelDSO.class));
        verify(eventPersistence, times(3)).eventExists(event1);
        verify(eventPersistence, times(2)).addLabel(event1, any(EventLabelDSO.class));
    }

    @Test (expected = EventNotFoundException.class)
    public void testRemoveLabelFromEvent() {
        EventLabelDSO label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");

        when(eventLabelPersistence.labelExists(any(EventLabelDSO.class))).thenReturn(true);
        when(eventPersistence.eventExists(event1)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(true);
        when(eventLabelPersistence.insertEventLabel(label1)).thenReturn(label2);
        when(eventPersistence.removeLabel(event1, label1)).thenReturn(event1);
        when(eventPersistence.removeLabel(event1, label2)).thenReturn(event1);

        eventManager.addLabelToEvent(event1, label1); // should throw exception

        when(eventPersistence.addLabel(event1, label1)).thenReturn(event1);
        when(eventPersistence.addLabel(event1, label2)).thenReturn(event1);

        assertTrue(eventManager.addLabelToEvent(event1, label1).getEventLabels().contains(label1));
        assertTrue(eventManager.addLabelToEvent(event1, label2).getEventLabels().contains(label2));

        EventDSO result = eventManager.removeLabelFromEvent(event1, label1);
        assertFalse(result.getEventLabels().contains(label1));
        result = eventManager.removeLabelFromEvent(event1, label2);
        assertFalse(result.getEventLabels().contains(label2));

        verify(eventLabelPersistence, times(2)).labelExists(any(EventLabelDSO.class));
        verify(eventLabelPersistence, times(1)).insertEventLabel(any(EventLabelDSO.class));
        verify(eventPersistence, times(3)).eventExists(event1);
        verify(eventPersistence, times(2)).addLabel(event1, any(EventLabelDSO.class));
        verify(eventPersistence, times(2)).removeLabel(event1, any(EventLabelDSO.class));
    }

    @Test
    public void testUpdateEventFavorite() {
        EventDSO testEventFav = new EventDSO(event1.getID(), event1.getDateCreated(), event1.getName());
        EventDSO testEventNotFav = new EventDSO(event1.getID(), event1.getDateCreated(), event1.getName());
        testEventFav.setFavorite(true);
        testEventNotFav.setFavorite(false);

        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);

        when(eventPersistence.updateEventFavorite(event1, true)).thenReturn(testEventFav);
        when(eventPersistence.updateEventFavorite(event1, false)).thenReturn(testEventNotFav);

        assertEquals("The event should be returned when true", event1, eventManager.updateEventFavorite(true, initialCount + 1));
        assertEquals("The event should be returned when false", event1, eventManager.updateEventFavorite(false, initialCount + 1));
        assertTrue("The event should be a favorite", eventManager.updateEventFavorite(true, initialCount + 1).isFavorite());
        assertFalse("The event should not be a favorite", eventManager.updateEventFavorite(false, initialCount + 1).isFavorite());

        verify(eventPersistence, times(4)).getEventByID(initialCount + 1);
        verify(eventPersistence, times(2)).updateEventFavorite(event1, true);
        verify(eventPersistence, times(2)).updateEventFavorite(event1, false);
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEvent() {
        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);
        when(eventPersistence.getEventByID(initialCount + 2)).thenReturn(event2);
        when(eventPersistence.getEventByID(initialCount + 3)).thenReturn(event3);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);

        when(eventPersistence.deleteEvent(any(EventDSO.class)))
                .thenReturn(event1).thenReturn(event2).thenReturn(event3);

        assertEquals("eventManager.deleteEvent(event1) should return event1",
                event1, eventManager.deleteEvent(initialCount + 1));
        assertEquals("eventManager.deleteEvent(event2) should return event2",
                event2, eventManager.deleteEvent(initialCount + 2));
        assertEquals("eventManager.deleteEvent(event3) should return event3",
                event3, eventManager.deleteEvent(initialCount + 3));

        verify(eventPersistence, times(3))
                .deleteEvent(any(EventDSO.class));

        assertEquals("eventManager.deleteEvent() of none existent event should throw exception",
                event3, eventManager.deleteEvent(-1));
    }

    @Test(expected = EventNotFoundException.class)
    public void testMarkEventAsDone() {
        EventDSO testEventC = new EventDSO(event1.getID(), currDate, "testComplete");
        EventDSO testEventIC = new EventDSO(event1.getID(), currDate, "testIncomplete");
        testEventC.setIsDone(true);
        testEventIC.setIsDone(false);
        when(eventPersistence.getEventByID(event1.getID())).thenReturn(event1).thenReturn(event1);
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);
        when(userPersistence.getUserByID(user.getID())).thenReturn(user);
        when(eventPersistence.updateEventStatus(event1,  true)).thenReturn(testEventC);
        when(eventPersistence.updateEventStatus(event1, false)).thenReturn(testEventIC);

        testEventC = eventManager.markEventAsDone(event1.getID(), true);
        assertTrue("event1 should be marked as done", testEventC.isDone());

        testEventIC = eventManager.markEventAsDone(event1.getID(), false);
        assertFalse("event1 should be marked as not done", testEventIC.isDone());

        verify(eventPersistence, times(2)).getEventByID(initialCount + 1);
        verify(eventPersistence, times(1)).updateEventStatus(event1, true);
        verify(eventPersistence, times(1)).updateEventStatus(event1, false);
        eventManager.markEventAsDone(-1, false); //should throw exception
    }

    @Test (expected = EventNotFoundException.class)
    public void testIsDone() {
        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);
        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);
        when(eventPersistence.getEventByID(initialCount + 2)).thenReturn(event2);

        event1.setIsDone(true);
        event2.setIsDone(false);

        assertTrue("event with id 1 should be done", eventManager.isDone(initialCount + 1));
        assertFalse("event with id 2 should not be done", eventManager.isDone(initialCount + 2));

        verify(eventPersistence).getEventByID(initialCount + 1);
        verify(eventPersistence).getEventByID(initialCount + 2);

        eventManager.isDone(-1); // should throw exception
    }

    @Test(expected = EventNotFoundException.class)
    public void testIsOverdue() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.set(9999, 10, 10);

        when(eventPersistence.getEventByID(-1)).thenThrow(EventNotFoundException.class);
        when(eventPersistence.getEventByID(initialCount + 1)).thenReturn(event1);
        when(eventPersistence.getEventByID(initialCount + 2)).thenReturn(event2);

        event1.setTargetFinishTime(Calendar.getInstance());
        event2.setTargetFinishTime(futureDate);

        assertTrue("event with id 1 should be done", eventManager.isOverdue(initialCount + 1));
        assertFalse("event with id 2 should not be done", eventManager.isOverdue(initialCount + 2));

        verify(eventPersistence).getEventByID(initialCount + 1);
        verify(eventPersistence).getEventByID(initialCount + 2);

        eventManager.isOverdue(-1); // should throw exception
    }

    @Test
    public void testInsertEvent() {
        EventLabelDSO eventLabel = new EventLabelDSO(initialLabelCount + 1, "eventLabel1");
        String eventName = "event1", eventDesc = "desc";

        when(eventPersistence.getNextID()).thenReturn(initialCount + 1)
                .thenReturn(initialCount + 2).thenReturn(initialCount + 3);
        when(eventLabelPersistence.getNextID()).thenReturn(initialLabelCount + 1)
                .thenReturn(initialLabelCount + 2).thenReturn(initialLabelCount + 3);
        when(userPersistence.getUserByEmail("userNotFound"))
                .thenThrow(UserNotFoundException.class);
        when(userPersistence.getUserByEmail("user1"))
                .thenReturn(user);
        when(eventPersistence.insertEvent(any(EventDSO.class)))
                .thenReturn(event1)
                .thenReturn(event1)
                .thenThrow(DuplicateEventException.class);
        when(eventLabelPersistence.insertEventLabel(any(EventLabelDSO.class)))
                .thenReturn(eventLabel);

        Assert.assertNotNull("eventManager.insertEvent(event1) should return event1",
                eventManager.createEvent(eventName, eventDesc,
                        Calendar.getInstance(), true));

        verify(eventPersistence, times(1)).insertEvent(any(EventDSO.class));

        eventManager.createEvent( eventName, eventDesc,
                Calendar.getInstance(), true); // should throw duplicateEventException
    }

    @Test
    public void testNumEvents() {
        when(eventPersistence.numEvents()).thenReturn(initialCount);
        assertEquals("eventManager.numEvents() should return " + initialCount,
                initialCount, eventManager.numEvents());
        verify(eventPersistence).numEvents();
    }

}

