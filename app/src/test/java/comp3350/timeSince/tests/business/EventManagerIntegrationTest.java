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
    private EventDSO event1, event2, event3;
    private Calendar currDate;
    private static boolean setupIsDone = false;

    @Before
    public void setUp() throws IOException {
        tempDB = TestUtils.copyDB();
        eventManager = new EventManager();
        userManager = new UserManager();
        currDate = Calendar.getInstance();

        event1 = new EventDSO(0, currDate, "event1");
        event2 = new EventDSO(1, currDate, "event2");
        event3 = new EventDSO(2, currDate, "event3");

        Assert.assertNotNull("should've inserted user1",
                userManager.insertUser(new UserDSO("user1", currDate, "hash1")));
        Assert.assertNotNull("should've inserted user2",
                userManager.insertUser(new UserDSO("user2", currDate, "hash2")));
        Assert.assertNotNull("should've inserted user3",
                userManager.insertUser(new UserDSO("user3", currDate, "hash3")));

        if(!setupIsDone) {
            eventManager.insertEvent("user1", currDate, "event1", "label1", true);
            eventManager.insertEvent("user2", currDate, "event2", "label2", true);
            eventManager.insertEvent("user3", currDate, "event3", "label3", true);
            setupIsDone = true;
        }
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
        assertEquals("The default event with ID 1 is 'event1'.",
                "event1", eventManager.getEventByID(1).getName());
        assertEquals("The default event with ID 2 is 'event2'.",
                "event2", eventManager.getEventByID(2).getName());
        assertEquals("The default event with ID 3 is 'event3'.",
                "event3", eventManager.getEventByID(3).getName());
        assertEquals("number of events should be 3", 3, eventManager.numEvents());
    }

    @Test
    public void testB_UpdateEvent(){
        EventDSO event = eventManager.getEventByID(1);
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
    public void testC_DeleteEvent() {
        assertNotNull("event1 should be deleted", eventManager.deleteEvent(1));
        assertNotNull("event2 should be deleted", eventManager.deleteEvent(2));
        assertNotNull("event3 should be deleted", eventManager.deleteEvent(3));
        assertEquals("there should be no events in the database", 0,
                eventManager.numEvents());

        //reset database
        eventManager.insertEvent("user1", currDate, "event1", "label1", true);
        eventManager.insertEvent("user2", currDate, "event2", "label2", true);
        eventManager.insertEvent("user3", currDate, "event3", "label3", true);
    }

    @Test
    public void testD_IsDone() {
        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10, 10);
        eventManager.getEventByID(1).setTargetFinishTime(cal);
        assertTrue("the event should be marked as done", eventManager.isDone(1));

        cal.set(9999, 10, 10);
        eventManager.getEventByID(1).setTargetFinishTime(cal);
        assertFalse("the event should not be marked as done", eventManager.isDone(1));

        eventManager.getEventByID(1).setIsDone(true);
        assertTrue("the event should be marked as done", eventManager.isDone(1));

        eventManager.getEventByID(1).setIsDone(false);
        assertFalse("the event should not be marked as done", eventManager.isDone(1));
    }

    @Test
    public void testE_InsertEvent(){
        assertNotNull("event4 should be inserted",
                eventManager.insertEvent("user1", currDate, "event4", "label4", true));
        assertNotNull("event5 should be inserted",
                eventManager.insertEvent("user1", currDate, "event5", "label5", false));
        assertNotNull("event6 should be inserted",
                eventManager.insertEvent("user1", currDate, "event6", "label6", true));

        EventDSO event4 = eventManager.getEventByID(4);
        EventDSO event5 = eventManager.getEventByID(5);
        EventDSO event6 = eventManager.getEventByID(6);

        assertEquals("event with id 4 should have the name 'event4'", "event4", event4.getName());
        assertTrue("event with id 4 should be a favorite", event4.isFavorite());
        assertEquals("event with id 5 should have the name 'event5'", "event5", event5.getName());
        assertFalse("event with id 5 should not be a favorite", event5.isFavorite());
        assertEquals("event with id 6 should have the name 'event6'", "event6", event6.getName());
        assertTrue("event with id 6 should be a favorite", event6.isFavorite());
        assertEquals("the database should contain 6 events", 6, eventManager.numEvents());
    }

    @Test
    public void testF_NumEvent() {
        assertEquals("There are 6 default events", 6, eventManager.numEvents());
    }
}
