package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.tests.utils.InitialDatabaseState;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class UserEventManagerTest {

    private UserEventManager userEventManager;
    private IEventPersistence eventPersistence;
    private IEventLabelPersistence labelPersistence;
    private UserDSO user;
    private EventDSO event1, event2, event3;
    private EventLabelDSO label1, label2, label3;
    private static final String testEmail = "testemail@outlook.com";
    private static final int initialUserCount = InitialDatabaseState.NUM_USERS;
    private static final int initialEventCount = InitialDatabaseState.NUM_EVENTS;
    private static final int initialLabelCount = InitialDatabaseState.NUM_LABELS;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();

        IUserPersistence userPersistence = Services.getUserPersistence(true);
        eventPersistence = Services.getEventPersistence(true);
        labelPersistence = Services.getEventLabelPersistence(true);

        Calendar testDate = Calendar.getInstance();
        user = new UserDSO(initialUserCount + 1, testEmail,
                testDate, "Password123");

        event1 = new EventDSO(initialEventCount + 1, testDate, "Event1");
        event2 = new EventDSO(initialEventCount + 2, testDate, "Event2");
        event3 = new EventDSO(initialEventCount + 3, testDate, "Event3");
        label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");
        label3 = new EventLabelDSO(initialLabelCount + 3, "Label3");

        user = userPersistence.insertUser(user);
        userEventManager = new UserEventManager(testEmail, true);
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test (expected = UserNotFoundException.class)
    public void testConstructorException() {
        userEventManager = new UserEventManager("badTest@outlook.com", true);
    }

    @Test
    public void testAddUserEvent() {
        assertNull("If the event is invalid, should return null", userEventManager.addUserEvent(null));

        UserDSO result = userEventManager.addUserEvent(event1);
        assertEquals("If the event is not in the database, should add it and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserEvents().contains(event1));

        event2 = eventPersistence.insertEvent(event2);
        result = userEventManager.addUserEvent(event2);
        assertEquals("If the event is in the database, should add it to the user and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserEvents().contains(event2));
    }

    @Test
    public void testAddUserFavorite() {
        assertNull("If the event is invalid, should return null", userEventManager.addUserFavorite(null));

        UserDSO result = userEventManager.addUserFavorite(event1);
        assertEquals("If the event is not in the database, should add it and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserFavorites().contains(event1));

        event2 = eventPersistence.insertEvent(event2);
        result = userEventManager.addUserFavorite(event2);
        assertEquals("If the event is in the database, should add it to the user and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserFavorites().contains(event2));
    }

    @Test
    public void testAddUserLabel() {
        assertNull("If the label is invalid, should return null", userEventManager.addUserLabel(null));

        UserDSO result = userEventManager.addUserLabel(label1);
        assertEquals("If the label is not in the database, should add it and return user", user, result);
        assertTrue("The label should be added to the user", result.getUserLabels().contains(label1));

        label2 = labelPersistence.insertEventLabel(label2);
        result = userEventManager.addUserLabel(label2);
        assertEquals("If the label is in the database, should add it to the user and return user", user, result);
        assertTrue("The label should be added to the user", result.getUserLabels().contains(label2));
    }

    @Test
    public void testRemoveUserEvent() {
        user = userEventManager.addUserEvent(event1);
        user = userEventManager.addUserEvent(event2);
        user = userEventManager.addUserEvent(event3);
        assertEquals("The user should have 3 events", 3, user.getUserEvents().size());

        user = userEventManager.removeUserEvent(event1);
        List<EventDSO> result = user.getUserEvents();
        assertEquals("The user should have 2 events", 2, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertTrue("The user should contain event2", result.contains(event2));
        assertTrue("The user should contain event3", result.contains(event3));

        user = userEventManager.removeUserEvent(event2);
        result = user.getUserEvents();
        user = userEventManager.removeUserEvent(event3);
        assertEquals("The user should have 0 events", 0, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertFalse("The user should not contain event2", result.contains(event2));
        assertFalse("The user should not contain event3", result.contains(event3));
    }

    @Test
    public void testRemoveUserFavorite() {
        user = userEventManager.addUserFavorite(event1);
        user = userEventManager.addUserFavorite(event2);
        user = userEventManager.addUserFavorite(event3);
        assertEquals("The user should have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 3 favorites", 3, user.getUserFavorites().size());

        user = userEventManager.removeUserFavorite(event1);
        List<EventDSO> result = user.getUserFavorites();
        assertEquals("The user should still have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 2 favorites", 2, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertTrue("The user should contain event2", result.contains(event2));
        assertTrue("The user should contain event3", result.contains(event3));

        user = userEventManager.removeUserFavorite(event2);
        result = user.getUserFavorites();
        user = userEventManager.removeUserFavorite(event3);
        assertEquals("The user should still have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 0 favorites", 0, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertFalse("The user should not contain event2", result.contains(event2));
        assertFalse("The user should not contain event3", result.contains(event3));
    }

    @Test
    public void testRemoveUserLabel() {
        user = userEventManager.addUserLabel(label1);
        user = userEventManager.addUserLabel(label2);
        user = userEventManager.addUserLabel(label3);
        assertEquals("The user should have 3 labels", 3, user.getUserLabels().size());

        user = userEventManager.removeUserLabel(label1);
        List<EventLabelDSO> result = user.getUserLabels();
        assertEquals("The user should have 2 labels", 2, result.size());
        assertFalse("The user should not contain label1", result.contains(label1));
        assertTrue("The user should contain label2", result.contains(label2));
        assertTrue("The user should contain label3", result.contains(label3));

        user = userEventManager.removeUserLabel(label2);
        result = user.getUserLabels();
        user = userEventManager.removeUserLabel(label3);
        assertEquals("The user should have 0 labels", 0, result.size());
        assertFalse("The user should not contain label1", result.contains(label1));
        assertFalse("The user should not contain label2", result.contains(label2));
        assertFalse("The user should not contain label3", result.contains(label3));
    }

    @Test
    public void testGetUserEvents() {
        assertEquals("If the user has no events, should return empty list",
                0, userEventManager.getUserEvents().size());
        user = userEventManager.addUserEvent(event1);
        user = userEventManager.addUserEvent(event2);
        user = userEventManager.addUserEvent(event3);

        assertEquals("The user should have 3 events",
                3, userEventManager.getUserEvents().size());
        assertTrue("The user should contain event1",
                userEventManager.getUserEvents().contains(event1));
        assertTrue("The user should contain event2",
                userEventManager.getUserEvents().contains(event2));
        assertTrue("The user should contain event3",
                userEventManager.getUserEvents().contains(event3));
    }

    @Test
    public void testGetUserLabels() {
        assertEquals("If the user has no labels, should return empty list",
                0, userEventManager.getUserLabels().size());
        user = userEventManager.addUserLabel(label1);
        user = userEventManager.addUserLabel(label2);
        user = userEventManager.addUserLabel(label3);

        assertEquals("The user should have 3 labels",
                3, userEventManager.getUserLabels().size());
        assertTrue("The user should contain label1",
                userEventManager.getUserLabels().contains(label1));
        assertTrue("The user should contain label2",
                userEventManager.getUserLabels().contains(label2));
        assertTrue("The user should contain label3",
                userEventManager.getUserLabels().contains(label3));
    }

    @Test
    public void testGetUserFavorites() {
        assertEquals("If the user has no events, should return empty list",
                0, userEventManager.getUserFavorites().size());
        user = userEventManager.addUserFavorite(event1);
        user = userEventManager.addUserFavorite(event2);
        user = userEventManager.addUserFavorite(event3);

        assertEquals("The user should have 3 favorite events",
                3, userEventManager.getUserFavorites().size());
        assertTrue("The user should contain event1",
                userEventManager.getUserFavorites().contains(event1));
        assertTrue("The user should contain event2",
                userEventManager.getUserFavorites().contains(event2));
        assertTrue("The user should contain event3",
                userEventManager.getUserFavorites().contains(event3));
    }

    @Test
    public void testCheckClosingEvents() {
        assertEquals("If the user has no events, there should be no closing events",
                0, userEventManager.checkClosingEvents().size());

        Calendar dueDate1 = Calendar.getInstance();
        Calendar dueDate2 = Calendar.getInstance();
        Calendar dueDate3 = Calendar.getInstance();
        dueDate1.add(Calendar.DAY_OF_YEAR, 2);
        dueDate2.add(Calendar.DAY_OF_YEAR, 8);
        dueDate3.add(Calendar.DAY_OF_YEAR, -1);
        event1.setTargetFinishTime(dueDate1);
        event2.setTargetFinishTime(dueDate2);
        event3.setTargetFinishTime(dueDate3);
        user = userEventManager.addUserEvent(event1);
        user = userEventManager.addUserEvent(event2);
        user = userEventManager.addUserEvent(event3);

        assertEquals("The user should have 2 coming events in 7 days",
                2, userEventManager.checkClosingEvents().size() );
        assertTrue("The user should contain event1",
                userEventManager.checkClosingEvents().contains(event1));
        assertFalse("The user should not contain event2",
                userEventManager.checkClosingEvents().contains(event2));
        assertTrue("The user should contain event3",
                userEventManager.checkClosingEvents().contains(event3));
    }

}
