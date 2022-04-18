package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

public class UserEventManagerTest {

    private UserEventManager userEventManagerBad;
    private UserEventManager userEventManagerGood;
    private IUserPersistence userPersistence;
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

        userPersistence = Services.getUserPersistence(true);
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

        userEventManagerBad = new UserEventManager(true);
        user = userPersistence.insertUser(user);
        userEventManagerGood = new UserEventManager(testEmail, true);
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testAddUserEvent() {
        assertNull("If the user has not been set in the database, should return null", userEventManagerBad.addUserEvent(event1));
        assertNull("If the event is invalid, should return null", userEventManagerGood.addUserEvent(null));

        UserDSO result = userEventManagerGood.addUserEvent(event1);
        assertEquals("If the event is not in the database, should add it and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserEvents().contains(event1));

        event2 = eventPersistence.insertEvent(event2);
        result = userEventManagerGood.addUserEvent(event2);
        assertEquals("If the event is in the database, should add it to the user and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserEvents().contains(event2));
    }

    @Test
    public void testAddUserFavorite() {
        assertNull("If the user has not been set in the database, should return null", userEventManagerBad.addUserFavorite(event1));
        assertNull("If the event is invalid, should return null", userEventManagerGood.addUserFavorite(null));

        UserDSO result = userEventManagerGood.addUserFavorite(event1);
        assertEquals("If the event is not in the database, should add it and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserFavorites().contains(event1));

        event2 = eventPersistence.insertEvent(event2);
        result = userEventManagerGood.addUserFavorite(event2);
        assertEquals("If the event is in the database, should add it to the user and return user", user, result);
        assertTrue("The event should be added to the user", result.getUserFavorites().contains(event2));
    }

    @Test
    public void testAddUserLabel() {
        assertNull("If the user has not been set in the database, should return null", userEventManagerBad.addUserLabel(label1));
        assertNull("If the label is invalid, should return null", userEventManagerGood.addUserLabel(null));

        UserDSO result = userEventManagerGood.addUserLabel(label1);
        assertEquals("If the label is not in the database, should add it and return user", user, result);
        assertTrue("The label should be added to the user", result.getUserLabels().contains(label1));

        label2 = labelPersistence.insertEventLabel(label2);
        result = userEventManagerGood.addUserLabel(label2);
        assertEquals("If the label is in the database, should add it to the user and return user", user, result);
        assertTrue("The label should be added to the user", result.getUserLabels().contains(label2));
    }

    @Test
    public void testRemoveUserEvent() {
        user = userEventManagerGood.addUserEvent(event1);
        user = userEventManagerGood.addUserEvent(event2);
        user = userEventManagerGood.addUserEvent(event3);
        assertEquals("The user should have 3 events", 3, user.getUserEvents().size());

        user = userEventManagerGood.removeUserEvent(event1);
        List<EventDSO> result = user.getUserEvents();
        assertEquals("The user should have 2 events", 2, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertTrue("The user should contain event2", result.contains(event2));
        assertTrue("The user should contain event3", result.contains(event3));

        user = userEventManagerGood.removeUserEvent(event2);
        result = user.getUserEvents();
        user = userEventManagerGood.removeUserEvent(event3);
        assertEquals("The user should have 0 events", 0, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertFalse("The user should not contain event2", result.contains(event2));
        assertFalse("The user should not contain event3", result.contains(event3));
    }

    @Test
    public void testRemoveUserFavorite() {
        user = userEventManagerGood.addUserFavorite(event1);
        user = userEventManagerGood.addUserFavorite(event2);
        user = userEventManagerGood.addUserFavorite(event3);
        assertEquals("The user should have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 3 favorites", 3, user.getUserFavorites().size());

        user = userEventManagerGood.removeUserFavorite(event1);
        List<EventDSO> result = user.getUserFavorites();
        assertEquals("The user should still have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 2 favorites", 2, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertTrue("The user should contain event2", result.contains(event2));
        assertTrue("The user should contain event3", result.contains(event3));

        user = userEventManagerGood.removeUserFavorite(event2);
        result = user.getUserFavorites();
        user = userEventManagerGood.removeUserFavorite(event3);
        assertEquals("The user should still have 3 events", 3, user.getUserEvents().size());
        assertEquals("The user should have 0 favorites", 0, result.size());
        assertFalse("The user should not contain event1", result.contains(event1));
        assertFalse("The user should not contain event2", result.contains(event2));
        assertFalse("The user should not contain event3", result.contains(event3));
    }

    @Test
    public void testRemoveUserLabel() {
        user = userEventManagerGood.addUserLabel(label1);
        user = userEventManagerGood.addUserLabel(label2);
        user = userEventManagerGood.addUserLabel(label3);
        assertEquals("The user should have 3 labels", 3, user.getUserLabels().size());

        user = userEventManagerGood.removeUserLabel(label1);
        List<EventLabelDSO> result = user.getUserLabels();
        assertEquals("The user should have 2 labels", 2, result.size());
        assertFalse("The user should not contain label1", result.contains(label1));
        assertTrue("The user should contain label2", result.contains(label2));
        assertTrue("The user should contain label3", result.contains(label3));

        user = userEventManagerGood.removeUserLabel(label2);
        result = user.getUserLabels();
        user = userEventManagerGood.removeUserLabel(label3);
        assertEquals("The user should have 0 labels", 0, result.size());
        assertFalse("The user should not contain label1", result.contains(label1));
        assertFalse("The user should not contain label2", result.contains(label2));
        assertFalse("The user should not contain label3", result.contains(label3));
    }

    @Test
    public void testGetUserEvents() {
        assertNull("If the user has not been set in the database, should return null",
                userEventManagerBad.getUserEvents());
        assertEquals("If the user has no events, should return empty list",
                0, userEventManagerGood.getUserEvents().size());
        user = userEventManagerGood.addUserEvent(event1);
        user = userEventManagerGood.addUserEvent(event2);
        user = userEventManagerGood.addUserEvent(event3);

        assertEquals("The user should have 3 events",
                3, userEventManagerGood.getUserEvents().size());
        assertTrue("The user should contain event1",
                userEventManagerGood.getUserEvents().contains(event1));
        assertTrue("The user should contain event2",
                userEventManagerGood.getUserEvents().contains(event2));
        assertTrue("The user should contain event3",
                userEventManagerGood.getUserEvents().contains(event3));
    }

    @Test
    public void testGetUserLabels() {
        assertNull("If the user has not been set in the database, should return null",
                userEventManagerBad.getUserLabels());

        assertEquals("If the user has no labels, should return empty list",
                0, userEventManagerGood.getUserLabels().size());
        user = userEventManagerGood.addUserLabel(label1);
        user = userEventManagerGood.addUserLabel(label2);
        user = userEventManagerGood.addUserLabel(label3);

        assertEquals("The user should have 3 labels",
                3, userEventManagerGood.getUserLabels().size());
        assertTrue("The user should contain label1",
                userEventManagerGood.getUserLabels().contains(label1));
        assertTrue("The user should contain label2",
                userEventManagerGood.getUserLabels().contains(label2));
        assertTrue("The user should contain label3",
                userEventManagerGood.getUserLabels().contains(label3));
    }

    @Test
    public void testGetUserFavorites() {
        assertNull("If the user has not been set in the database, should return null",
                userEventManagerBad.getUserFavorites());

        assertEquals("If the user has no events, should return empty list",
                0, userEventManagerGood.getUserFavorites().size());
        user = userEventManagerGood.addUserFavorite(event1);
        user = userEventManagerGood.addUserFavorite(event2);
        user = userEventManagerGood.addUserFavorite(event3);

        assertEquals("The user should have 3 favorite events",
                3, userEventManagerGood.getUserFavorites().size());
        assertTrue("The user should contain event1",
                userEventManagerGood.getUserFavorites().contains(event1));
        assertTrue("The user should contain event2",
                userEventManagerGood.getUserFavorites().contains(event2));
        assertTrue("The user should contain event3",
                userEventManagerGood.getUserFavorites().contains(event3));
    }

}
