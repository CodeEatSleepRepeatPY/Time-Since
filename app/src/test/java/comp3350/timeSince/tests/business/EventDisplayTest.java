package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class EventDisplayTest {

    private EventManager eventManager;
    private UserEventManager userEventManager;
    private IUserPersistence userPersistence;
    private UserDSO user;
    private EventDSO event1, event2, event3;
    private EventLabelDSO label1, label2, label3;
    private static final int initialUserCount = InitialDatabaseState.NUM_USERS;
    private static final int initialEventCount = InitialDatabaseState.NUM_EVENTS;
    private static final int initialLabelCount = InitialDatabaseState.NUM_LABELS;

    @Rule
    public ExpectedException exceptionRule;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();

        userPersistence = Services.getUserPersistence(true);

        Calendar date1, date2, date3;
        date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 5);
        date2 = Calendar.getInstance();
        date2.add(Calendar.DATE, 1);
        date3 = Calendar.getInstance();
        date3.add(Calendar.DATE, 3);

        user = new UserDSO(initialUserCount + 1, "testUser@gmail.com",
                Calendar.getInstance(), "hash1");

        event1 = new EventDSO(initialEventCount + 1, date1, "event1");
        event2 = new EventDSO(initialEventCount + 2, date2, "event2");
        event3 = new EventDSO(initialEventCount + 3, date3, "event3");

        label1 = new EventLabelDSO(initialLabelCount + 1, "label1");
        label2 = new EventLabelDSO(initialLabelCount + 2, "label2");
        label3 = new EventLabelDSO(initialLabelCount + 3, "label3");

        user = userPersistence.insertUser(user);
        eventManager = new EventManager(true);
        userEventManager = new UserEventManager(user.getEmail(), true);
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetEventsByStatus() {
        user = userPersistence.addUserEvent(user, event1);
        user = userPersistence.addUserEvent(user, event2);
        user = userPersistence.addUserEvent(user, event3);

        List<EventDSO> result = userEventManager.filterByStatus(true);
        assertEquals("All events should be incomplete on default", 0, result.size());
        result = userEventManager.filterByStatus(false);
        assertEquals("All events should be incomplete on default", 3, result.size());

        event1 = eventManager.markEventAsDone(event1.getID(), true);
        event2 = eventManager.markEventAsDone(event2.getID(), false);
        event3 = eventManager.markEventAsDone(event3.getID(), true);

        result = userEventManager.filterByStatus(true);
        assertEquals("The user should have 2 completed events", 2, result.size());
        assertTrue("The list should contain event1", result.contains(event1));
        assertFalse("The list should not contain event2", result.contains(event2));
        assertTrue("The list should contain event3", result.contains(event3));

        result = userEventManager.filterByStatus(false);
        assertEquals("The user should have 1 incomplete event", 1, result.size());
        assertFalse("The list should not contain event1", result.contains(event1));
        assertTrue("The list should contain event2", result.contains(event2));
        assertFalse("The list should not contain event3", result.contains(event3));
    }

    @Test
    public void testGetEventsByLabel() {
        event1.addLabel(label1);
        event1.addLabel(label2);
        event2.addLabel(label2);
        event2.addLabel(label3);
        event3.addLabel(label1);
        event3.addLabel(label2);

        user = userPersistence.addUserEvent(user, event1);
        user = userPersistence.addUserEvent(user, event2);
        user = userPersistence.addUserEvent(user, event3);
        assertNotNull("The user should not be null after inserting all events", user);

        List<EventDSO> result = userEventManager.filterByLabel(label1.getID());
        assertTrue("Label1 should return event1", result.contains(event1));
        assertTrue("Label1 should return event3", result.contains(event3));
        assertEquals("The user has 2 events with label1", 2, result.size());

        result = userEventManager.filterByLabel(label2.getID());
        assertEquals("The user has 3 events with label2", 3, result.size());
        assertTrue("Label2 should return event1", result.contains(event1));
        assertTrue("Label2 should return event2", result.contains(event2));
        assertTrue("Label2 should return event3", result.contains(event3));

        result = userEventManager.filterByLabel(label3.getID());
        assertEquals("The user has 1 event with label3", 1, result.size());
        assertTrue("Label3 should return event2", result.contains(event2));
    }

    @Test
    public void testGetEventsByDateCreated() {
        user = userPersistence.addUserEvent(user, event2);
        user = userPersistence.addUserEvent(user, event3);
        user = userPersistence.addUserEvent(user, event1);

        List<EventDSO> result = userEventManager.sortByDateCreated(true);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event1", event1, result.get(0));
        assertEquals("The second event should be event3", event3, result.get(1));
        assertEquals("The third event should be event2", event2, result.get(2));

        result = userEventManager.sortByDateCreated(false);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event2", event2, result.get(0));
        assertEquals("The second event should be event3", event3, result.get(1));
        assertEquals("The third event should be event1", event1, result.get(2));

    }

    @Test
    public void testGetEventsByDueDate() {
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 1);
        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DATE, 2);

        event3.setTargetFinishTime(date1);
        event1.setTargetFinishTime(date2);
        // event2 is null

        user = userPersistence.addUserEvent(user, event1);
        user = userPersistence.addUserEvent(user, event2);
        user = userPersistence.addUserEvent(user, event3);

        List<EventDSO> result = userEventManager.sortByFinishTime(true);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event3", event3, result.get(0));
        assertEquals("The second event should be event1", event1, result.get(1));
        assertEquals("The third event should be event2", event2, result.get(2));

        result = userEventManager.sortByFinishTime(false);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event1", event1, result.get(0));
        assertEquals("The second event should be event3", event3, result.get(1));
        assertEquals("The third event should be event2", event2, result.get(2));
    }

    @Test
    public void testGetEventsAlphabetical() {
        user = userPersistence.addUserEvent(user, event2);
        user = userPersistence.addUserEvent(user, event3);
        user = userPersistence.addUserEvent(user, event1);

        List<EventDSO> result = userEventManager.sortByName(true);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event1", event1, result.get(0));
        assertEquals("The second event should be event2", event2, result.get(1));
        assertEquals("The third event should be event3", event3, result.get(2));

        result = userEventManager.sortByName(false);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event3", event3, result.get(0));
        assertEquals("The second event should be event2", event2, result.get(1));
        assertEquals("The third event should be event1", event1, result.get(2));
    }

}
