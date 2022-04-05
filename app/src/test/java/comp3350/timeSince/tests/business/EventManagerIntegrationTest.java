package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

public class EventManagerIntegrationTest {

    private EventManager eventManager;
    private UserDSO user;
    private Calendar currDate;

    @Before
    public void setUp() throws IOException {
        File tempDB = TestUtils.copyDB();
        currDate = Calendar.getInstance();
        user = new UserDSO("admin", currDate, "12345");
        eventManager = new EventManager(user.getID(), true);

        assertNotNull(eventManager);
        assertNotNull(tempDB);
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetEventByID() {

    }

    @Test
    public void testInsertEvent() {
        assertNotNull("event7 should be inserted",
                eventManager.insertEvent(currDate, "event7",
                        "label7", "desc7", true));
        assertNotNull("event8 should be inserted",
                eventManager.insertEvent(currDate, "event8",
                        "label8", "desc8", false));
        assertNotNull("event9 should be inserted",
                eventManager.insertEvent(currDate, "event9",
                        "label9", "desc9", true));

        EventDSO event7 = eventManager.getEventByID("event7");
        EventDSO event8 = eventManager.getEventByID("event8");
        EventDSO event9 = eventManager.getEventByID("event9");

        assertEquals("event with id 7 should have the name 'event7'",
                "event7", event7.getName());
        assertEquals("event with id 7 should have the description 'desc7'",
                "desc7", event7.getDescription());
        assertTrue("event with id 7 should be a favorite",
                event7.isFavorite());

        assertEquals("event with id 8 should have the name 'event8'",
                "event8", event8.getName());
        assertEquals("event with id 8 should have the description 'desc8'",
                "desc8", event8.getDescription());
        assertFalse("event with id 8 should not be a favorite",
                event8.isFavorite());

        assertEquals("event with id 9 should have the name 'event9'",
                "event9", event9.getName());
        assertEquals("event with id 9 should have the description 'desc9'",
                "desc9", event9.getDescription());
        assertTrue("event with id 9 should be a favorite",
                event9.isFavorite());

        assertEquals("the database should contain 9 events",
                9, eventManager.numEvents(user));
    }

    @Test
    public void testUpdateEvent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10, 10);

        assertNotNull("event description should be updated",
                eventManager.updateEventDescription("newEventDesc1", "New Toothbrush"));
        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, "New Toothbrush"));
        assertNotNull("the isEventFavorite field should be updated",
                eventManager.updateEventFavorite(true, "New Toothbrush"));

        EventDSO event = eventManager.getEventByID("New Toothbrush");

        assertEquals("event description should be 'newEventDesc1'", "newEventDesc1",
                event.getDescription());
        assertEquals("event finish time should be updated to the year '1990'", cal.getTime(),
                event.getTargetFinishTime().getTime());
        assertTrue("the isEventFavorite field should be true",
                event.isFavorite());
    }

    @Test
    public void testDeleteEvent() {
        assertEquals("there should be 6 events in the database to start",
                6, eventManager.numEvents(user));

        assertNotNull("'New Toothbrush' should be deleted", eventManager.deleteEvent("New Toothbrush"));
        assertNotNull("'Wash Sheets' should be deleted", eventManager.deleteEvent("Wash Sheets"));
        assertNotNull("'Workout' should be deleted", eventManager.deleteEvent("Workout"));

        assertEquals("there should be 3 events in the database now",
                3, eventManager.numEvents(user));

        assertNull("event with name 'New Toothbrush' should not exist", eventManager.getEventByID("New Toothbrush"));
        assertNull("event with name 'Wash Sheets' should not exist", eventManager.getEventByID("Wash Sheets"));
        assertNull("event with name 'Workout' should not exist", eventManager.getEventByID("Workout"));
    }

    @Test
    public void testMarkEventAsDone() {
        String eventName = "New Toothbrush";
        assertFalse("The event should not be done by default",
                eventManager.isDone(eventName));

        eventManager.markEventAsDone(eventName, true);
        assertTrue("The event should now be considered done",
                eventManager.isDone(eventName));

        eventManager.markEventAsDone(eventName, false);
        assertFalse("The event should now be considered not done",
                eventManager.isDone(eventName));
    }

    @Test
    public void testIsOverdue() {
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10, 10);
        String eventName = "Workout";

        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, eventName));
        assertTrue("the event should be marked as overdue",
                eventManager.isOverdue(eventName));

        cal.set(9999, 10, 10);
        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, eventName));
        assertFalse("the event should not be marked as overdue",
                eventManager.isOverdue(eventName));
    }

    @Test
    public void testNumEvent() {
        assertEquals("There should be 7 events in the database to start",
                7, eventManager.numEvents());
        assertEquals("There should be 6 events in the database for 'admin' to start",
                6, eventManager.numEvents(user));
    }
}
