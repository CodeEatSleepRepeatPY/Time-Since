package comp3350.timeSince.tests.business;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

public class EventMangerIntegrationTest {

    private EventManager eventManager;
    private File tempDB;
    private EventDSO event1, event2, event3;
    private Calendar currDate;

    @Before
    public void setUp() throws IOException {
        File tempDB = TestUtils.copyDB();
        eventManager = new EventManager(true);
        currDate = Calendar.getInstance();
        event1 = new EventDSO(0, currDate, "event1");
        event2 = new EventDSO(1, currDate, "event2");
        event3 = new EventDSO(2, currDate, "event3");
        assertNotNull(eventManager);
        assertNotNull(tempDB);
    }

    @After
    public void tearDown() {
        //tempDB.delete();
    }

    @Test
    public void testGetEventByID() {
        assertEquals("The default event with ID 1 is 'New Toothbrush'.",
                "New Toothbrush",
                eventManager.getEventByID(1).getName());
    }

    @Test
    public void testNumEvent() {
        assertEquals("There are 6 default events", 6, eventManager.numEvents());
    }

}
