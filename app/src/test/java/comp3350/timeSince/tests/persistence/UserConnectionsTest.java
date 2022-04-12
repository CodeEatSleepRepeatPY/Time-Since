package comp3350.timeSince.tests.persistence;
// TODO: fix this
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserConnectionsPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class UserConnectionsTest {

    private IUserConnectionsPersistence connectionsPersistence;
    private IUserPersistence userPersistence;
    private UserDSO user;
    private EventDSO event1, event2, event3;
    private EventLabelDSO label1, label2, label3;
    private Calendar date1, date2, date3;
    private final String testEmail = "uid";
    private static final int initialUserCount = 2;
    private static final int initialEventCount = 6;
    private static final int initialLabelCount = 6;

    @Rule
    public ExpectedException exceptionRule;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();
        connectionsPersistence = Services.getUserConnectionsPersistence();
        userPersistence = Services.getUserPersistence(true);

        date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 5);
        date2 = Calendar.getInstance();
        date2.add(Calendar.DATE, 1);
        date3 = Calendar.getInstance();
        date3.add(Calendar.DATE, 3);

        user = new UserDSO(initialUserCount + 1, testEmail,
                Calendar.getInstance(), "hash1");

        event1 = new EventDSO(initialEventCount + 1, date1, "Change bedding");
        event2 = new EventDSO(initialEventCount + 2, date2, "New toothbrush");
        event3 = new EventDSO(initialEventCount + 3, date3, "Take out garbage");

        label1 = new EventLabelDSO(initialLabelCount + 1, "label1");
        label2 = new EventLabelDSO(initialLabelCount + 2, "label2");
        label3 = new EventLabelDSO(initialLabelCount + 3, "label3");

        userPersistence.insertUser(user);
    }

    @After
    public void tearDown() {
        userPersistence.deleteUser(user);
        Services.clean();
    }

    @Test
    public void testGetAllEvents() {
        user = connectionsPersistence.addUserEvent(user, event1);
        user = connectionsPersistence.addUserEvent(user, event2);
        List<EventDSO> events = connectionsPersistence.getAllEvents(user);
        assertEquals("The user should have 2 events", 2, events.size());
        assertTrue("The user should contain event1", events.contains(event1));
        assertTrue("The user should contain event2", events.contains(event2));

        connectionsPersistence.removeUserEvent(user, event1);
        events = connectionsPersistence.getAllEvents(user);
        assertEquals("The user should have 1 event", 1, events.size());
        assertFalse("The user should not contain event1",
                events.contains(event1));
        assertTrue("The user should contain event2", events.contains(event2));
    }

    @Test
    public void testGetAllLabels() {
        assertEquals("The user should not have any labels to start",
                0, connectionsPersistence.getAllLabels(user).size());

        connectionsPersistence.addUserLabel(user, label1);
        connectionsPersistence.addUserLabel(user, label2);

        List<EventLabelDSO> result = connectionsPersistence.getAllLabels(user);
        assertEquals("The user should now have 2 labels",
                2, result.size());
        assertTrue(result.contains(label1));
        assertTrue(result.contains(label2));

        UserDSO testUser = new UserDSO(initialUserCount + 2, "email2",
                Calendar.getInstance(), "1234");
        userPersistence.insertUser(testUser);
        connectionsPersistence.addUserLabel(testUser, label3);

        result = connectionsPersistence.getAllLabels(user);
        assertEquals("The user should only have their own labels", 2, result.size());
        assertTrue(result.contains(label1));
        assertTrue(result.contains(label2));
        assertFalse(result.contains(label3));
    }

    @Test
    public void testGetFavorites() {
        user = connectionsPersistence.addFavorite(user, event1);
        user = connectionsPersistence.addFavorite(user, event2);
        user = connectionsPersistence.addFavorite(user, event3);

        user = connectionsPersistence.removeFavorite(user, event1);

    }

    @Test
    public void testGetEventsByStatus() {
        user = connectionsPersistence.addUserEvent(user, event1);
        user = connectionsPersistence.addUserEvent(user, event2);
        user = connectionsPersistence.addUserEvent(user, event3);

        List<EventDSO> result = connectionsPersistence.getEventsByStatus(user, true);
        assertEquals("All events should be incomplete on default", 0, result.size());
        result = connectionsPersistence.getEventsByStatus(user, false);
        assertEquals("All events should be incomplete on default", 3, result.size());

        user = connectionsPersistence.setStatus(user, event1, true);
        user = connectionsPersistence.setStatus(user, event2, false);
        user = connectionsPersistence.setStatus(user, event3, true);

        result = connectionsPersistence.getEventsByStatus(user, true);
        assertEquals("The user should have 2 completed events", 2, result.size());
        assertTrue("The list should contain event1", result.contains(event1));
        assertFalse("The list should not contain event2", result.contains(event2));
        assertTrue("The list should contain event3", result.contains(event3));

        result = connectionsPersistence.getEventsByStatus(user, false);
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

        user = connectionsPersistence.addUserEvent(user, event1);
        user = connectionsPersistence.addUserEvent(user, event2);
        user = connectionsPersistence.addUserEvent(user, event3);
        assertNotNull("The user should not be null after inserting all events", user);

        List<EventDSO> result = connectionsPersistence.getEventsByLabel(user, label1);
        assertTrue("Label1 should return event1", result.contains(event1));
        assertTrue("Label1 should return event3", result.contains(event3));
        assertEquals("The user has 2 events with label1", 2, result.size());

        result = connectionsPersistence.getEventsByLabel(user, label2);
        assertEquals("The user has 3 events with label2", 3, result.size());
        assertTrue("Label2 should return event1", result.contains(event1));
        assertTrue("Label2 should return event2", result.contains(event2));
        assertTrue("Label2 should return event3", result.contains(event3));

        result = connectionsPersistence.getEventsByLabel(user, label3);
        assertEquals("The user has 1 event with label3", 1, result.size());
        assertTrue("Label3 should return event2", result.contains(event2));
    }

    @Test
    public void testGetEventsByDateCreated() {

    }

    @Test
    public void testGetEventsAlphabetical() {
        user = connectionsPersistence.addUserEvent(user, event2);
        user = connectionsPersistence.addUserEvent(user, event3);
        user = connectionsPersistence.addUserEvent(user, event1);

        List<EventDSO> result = connectionsPersistence.getEventsAlphabetical(user, true);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event1", event1, result.get(0));
        assertEquals("The second event should be event2", event2, result.get(1));
        assertEquals("The third event should be event3", event3, result.get(2));

        result = connectionsPersistence.getEventsAlphabetical(user, false);
        assertNotNull("The returned list should not be null", result);
        assertEquals("There should be 3 events", 3, result.size());
        assertEquals("The first event should be event3", event3, result.get(0));
        assertEquals("The second event should be event2", event2, result.get(1));
        assertEquals("The third event should be event1", event1, result.get(2));
    }

    @Test
    public void testAddUserEvent() {

    }

    @Test
    public void testRemoveUserEvent() {

    }

    @Test
    public void testAddUserLabel() {
        assertEquals("The user should not have any labels to start",
                0, connectionsPersistence.getAllLabels(user).size());

        UserDSO result = connectionsPersistence.addUserLabel(user, label1);
        assertNotNull("The user should not be null after adding label1", result);
        result = userPersistence.getUserByEmail(testEmail);
        assertNotNull("The user should be found in the database", result);
        assertEquals("The user should have 1 label", 1, result.getUserLabels().size());
        assertTrue(result.getUserLabels().contains(label1));

        result = connectionsPersistence.addUserLabel(user, label2);
        assertNotNull("The user should not be null after adding label2", result);
        result = userPersistence.getUserByEmail(testEmail);
        assertNotNull("The user should be found in the database", result);
        assertEquals("The user should have 2 label", 2, result.getUserLabels().size());
        assertTrue(result.getUserLabels().contains(label2));
    }

    @Test
    public void testRemoveUserLabel() {

    }

    @Test
    public void testAddFavorite() {
        user = connectionsPersistence.addFavorite(user, event1);
        assertNotNull("User should not be null after adding event1 as " + "favorite", user);
        user = connectionsPersistence.addFavorite(user, event2);
        assertNotNull("User should not be null after adding event2 as " + "favorite", user);
        user = userPersistence.getUserByEmail(testEmail);
        assertEquals("User should have 2 favorites",
                2, connectionsPersistence.getFavorites(user).size());
    }

    @Test
    public void testRemoveFavorite() {
        System.out.println("------------------------------");
        System.out.println(userPersistence.getUserList().toString());
        System.out.println("------------------------------");
        user = connectionsPersistence.addFavorite(user, event1);
        user = connectionsPersistence.addFavorite(user, event2);

        user = connectionsPersistence.removeFavorite(user, event1);
        assertNotNull("User should not be null after removing event1 as " + "favorite", user);
        assertEquals("User should have 1 favorite",
                1, connectionsPersistence.getFavorites(user).size());
        assertFalse("Removed favorite should no longer exist",
                connectionsPersistence.getFavorites(user).contains(event1));

        user = connectionsPersistence.removeFavorite(user, event3);
        assertNotNull("User should not be null after removing event3 as " + "favorite", user);
        assertEquals("Removing a favorite not in the list should do nothing",
                1, connectionsPersistence.getFavorites(user).size());
        assertTrue("User should still have a favorite",
                connectionsPersistence.getFavorites(user).contains(event2));
    }

}
