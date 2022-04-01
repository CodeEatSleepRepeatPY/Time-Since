package comp3350.timeSince.tests.business;

import static org.junit.Assert.*;

import android.media.metrics.Event;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.tests.persistence.utils.TestUtils;
import comp3350.timeSince.business.UserManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventManagerIntegrationTest {

    private EventManager eventManager;
    UserManager userManager;
    private File tempDB;
    private Calendar currDate;

    @Before
    public void setUp() throws IOException {
        tempDB = TestUtils.copyDB();
        eventManager = new EventManager(true);
        userManager = new UserManager(true);
        currDate = Calendar.getInstance();

        assertNotNull(eventManager);
        assertNotNull(userManager);
        assertNotNull(tempDB);
    }

    @After
    public void tearDown() {
        //tempDB.delete();
    }

    @Test
    public void testA_GetEventByID() {
        assertEquals("The default event with ID 1 is 'New Toothbrush'.",
                "New Toothbrush", eventManager.getEventByID(1).getName());
        assertEquals("The default event with ID 2 is 'Wash Sheets'.",
                "Wash Sheets", eventManager.getEventByID(2).getName());
        assertEquals("The default event with ID 3 is 'Clean Shower'.",
                "Clean Shower", eventManager.getEventByID(3).getName());
        assertEquals("number of events should be 6", 6, eventManager.numEvents());
    }

    @Test
    public void testB_UpdateEvent(){
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10, 10);

        assertNotNull("event name should be updated",
                eventManager.updateEventName("newEventName1", 1));
        assertNotNull("event description should be updated",
                eventManager.updateEventDescription("newEventDesc1", 1));
        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, 1));
        assertNotNull("the isEventFavorite field should be updated",
                eventManager.updateEventFavorite(true, 1));

        EventDSO event = eventManager.getEventByID(1);

        assertEquals("event name should be 'newEventName1'", "newEventName1",
                event.getName());
        assertEquals("event description should be 'newEventDesc1'", "newEventDesc1",
                event.getDescription());
        assertEquals("event finish time should be updated to the year '1990'", cal.getTime(),
                event.getTargetFinishTime().getTime());
        assertTrue("the isEventFavorite field should be true",
                event.isFavorite());

        // reset to old value
        eventManager.updateEventName("event1", 1);


    }

    @Test
    public void testE_DeleteEvent() {
        assertNotNull("event1 should be deleted", eventManager.deleteEvent(1));
        assertNotNull("event2 should be deleted", eventManager.deleteEvent(2));
        assertNotNull("event3 should be deleted", eventManager.deleteEvent(3));
        assertEquals("there should be 6 events in the database", 6,
                eventManager.numEvents());
        assertNull("event with id 1 should not exist", eventManager.getEventByID(1));
        assertNull("event with id 2 should not exist", eventManager.getEventByID(2));
        assertNull("event with id 3 should not exist", eventManager.getEventByID(3));
    }

    @Test
    public void testC_IsDone() {
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10, 10);

        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, 1));
        assertTrue("the event should be marked as done", eventManager.isDone(1));

        cal.set(9999, 10, 10);
        assertNotNull("event finish time should be updated",
                eventManager.updateEventFinishTime(cal, 1));
        assertFalse("the event should not be marked as done", eventManager.isDone(1));
    }

    @Test
    public void testD_InsertEvent(){
        assertNotNull("event4 should be inserted",
                eventManager.insertEvent("admin", currDate, "event7", "label7", "desc7", true));
        assertNotNull("event5 should be inserted",
                eventManager.insertEvent("admin", currDate, "event8", "label8", "desc8", false));
        assertNotNull("event6 should be inserted",
                eventManager.insertEvent("admin", currDate, "event9", "label9", "desc9", true));

        EventDSO event7 = eventManager.getEventByID(7);
        EventDSO event8 = eventManager.getEventByID(8);
        EventDSO event9 = eventManager.getEventByID(9);

        assertEquals("event with id 7 should have the name 'event7'", "event7", event7.getName());
        assertEquals("event with id 7 should have the description 'desc7'", "desc7", event7.getDescription());
        assertTrue("event with id 7 should be a favorite", event7.isFavorite());
        assertEquals("event with id 8 should have the name 'event8'", "event8", event8.getName());
        assertEquals("event with id 7 should have the description 'desc8'", "desc8", event8.getDescription());
        assertFalse("event with id 8 should not be a favorite", event8.isFavorite());
        assertEquals("event with id 9 should have the name 'event9'", "event9", event9.getName());
        assertEquals("event with id 7 should have the description 'desc9'", "desc9", event9.getDescription());
        assertTrue("event with id 9 should be a favorite", event9.isFavorite());
        assertEquals("the database should contain 9 events", 9, eventManager.numEvents());
    }

    @Test
    public void testF_NumEvent() {
        assertEquals("There should be 6 events in the database", 6, eventManager.numEvents());
    }
}
