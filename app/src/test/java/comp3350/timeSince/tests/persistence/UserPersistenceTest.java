package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

@FixMethodOrder(MethodSorters.JVM)
public class UserPersistenceTest {

    private IUserPersistence userDatabase;
    private UserDSO user1, user2, user3;
    private EventDSO event1, event2;
    private EventLabelDSO label1, label2, label3;
    private List<UserDSO> userList;
    private Calendar defaultDate;
    private static final int initialUserCount = 2;
    private static final int initialEventCount = 8;
    private static final int initialLabelCount = 9;

    @Rule
    public ExpectedException exceptionRule;

    @Before
    public void setUp() {
        userDatabase = Services.getUserPersistence(false);
        defaultDate = Calendar.getInstance();
        user1 = new UserDSO(initialUserCount + 1, "uid1", defaultDate, "hash1");
        user2 = new UserDSO(initialUserCount + 2, "uid2", defaultDate, "hash2");
        user3 = new UserDSO(initialUserCount + 3, "uid3", defaultDate, "hash3");
        userList = new ArrayList<>(Arrays.asList(user1, user2, user3));

        event1 = new EventDSO(initialEventCount + 1, defaultDate, "event1");
        event2 = new EventDSO(initialEventCount + 2, defaultDate, "event2");

        label1 = new EventLabelDSO(initialLabelCount + 1, "label1");
        label2 = new EventLabelDSO(initialLabelCount + 2, "label2");
        label3 = new EventLabelDSO(initialLabelCount + 3, "label3");
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetUserList() {
        assertNotNull("Newly created database object should not be null",
                userDatabase);
        assertEquals("Newly created database should have " + initialUserCount + " users",
                initialUserCount, userDatabase.numUsers());
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);
        List<UserDSO> actual = userDatabase.getUserList();
        assertEquals("Size of database should be " + (initialUserCount + 3), initialUserCount + 3, actual.size());
        assertTrue("Database should contain user1", actual.contains(user1));
        assertTrue("Database should contain user2", actual.contains(user2));
        assertTrue("Database should contain user3", actual.contains(user3));
        assertTrue("Database should have all existing users",
                actual.containsAll(userList));
    }

    @Test
    public void testGetUserByID() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        UserDSO actual = userDatabase.getUserByID(initialUserCount + 1);
        assertEquals("The correct user should be returned if present",
                user1, actual);
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetUserByIDException() {
        userDatabase.getUserByID(-1);
    }

    @Test
    public void testGetUserByEmail() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        UserDSO actual = userDatabase.getUserByEmail("uid1");
        assertEquals("The correct user should be returned if present",
                user1, actual);
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetUserByEmailException() {
        userDatabase.getUserByEmail("uid4"); // should not be able to get user not in db
    }

    @Test
    public void testInsertUser() {
        assertEquals("Size of database should be " + initialUserCount,
                initialUserCount, userDatabase.numUsers());
        assertNotNull(userDatabase.insertUser(user1));
        assertEquals("Size of database should be " + (initialUserCount + 1),
                initialUserCount + 1, userDatabase.numUsers());

        userDatabase.insertUser(user2);
        assertEquals("Size of database should be " + (initialUserCount + 2),
                initialUserCount + 2, userDatabase.numUsers());

        userDatabase.insertUser(user3);
        assertEquals("Size of database should be " + (initialUserCount + 3),
                initialUserCount + 3, userDatabase.numUsers());
    }

    @Test(expected = DuplicateUserException.class)
    public void testInsertUserException() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user1);
        assertEquals("Should not be able to insert a duplicate user",
                initialUserCount + 1, userDatabase.numUsers());
    }

    @Test
    public void testUpdateUser() {
        userDatabase.insertUser(user1);
        assertEquals("Size of database should be " + (initialUserCount + 1), initialUserCount + 1,
                userDatabase.numUsers());

        user1 = userDatabase.updateUserName(user1, "hello");
        assertEquals("New attributes should match", "hello",
                userDatabase.getUserByEmail("uid1").getName());
    }

    @Test (expected = UserNotFoundException.class)
    public void testUpdateUserException() {
        userDatabase.updateUserName(user1, "not present"); // should not be able to update user not in db
    }

    @Test
    public void testUpdateUserEmail() {
        user1 = userDatabase.insertUser(user1);
        String newEmail = "bobby2@gmail.com";
        String message = String.format("The email should now be set to %s", newEmail);
        user1 = userDatabase.updateUserEmail(user1, newEmail);
        assertEquals(message, newEmail, user1.getEmail());
    }

    @Test
    public void testUpdateUserPassword() {
        user1 = userDatabase.insertUser(user1);
        String newPassword = "Password12345";
        user1 = userDatabase.updateUserPassword(user1, newPassword);
        assertEquals("The password should be updated", newPassword, user1.getPasswordHash());
    }

    @Test
    public void testDeleteUser() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);

        assertEquals("Size of database should be " + (initialUserCount + 3), initialUserCount + 3,
                userDatabase.numUsers());

        assertEquals("If user exists, return the user that was deleted",
                user1, userDatabase.deleteUser(user1));

        assertEquals("Size of database should be " + (initialUserCount + 2), initialUserCount + 2,
                userDatabase.numUsers());
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUserException() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);
        userDatabase.deleteUser(new UserDSO(initialUserCount + 4, "uid4", defaultDate, "password"));
        assertEquals("Size of database should be " + (initialUserCount + 3), initialUserCount + 3, userDatabase.numUsers());
    }

    @Test
    public void testIsUnique() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        assertTrue("A unique user should be considered unique",
                userDatabase.isUnique(user3.getEmail()));
        assertFalse("User should not be unique if one already exists",
                userDatabase.isUnique(user1.getEmail()));
    }

    @Test (expected = DuplicateUserException.class)
    public void testGetNextID() {
        assertEquals("The first ID should be " + (initialUserCount + 1),
                initialUserCount + 1, userDatabase.getNextID());
        userDatabase.insertUser(user1);
        assertEquals("The ID of the first event inserted should be " + (initialUserCount + 1),
                initialUserCount + 1, user1.getID());

        assertEquals("The second ID should be " + (initialUserCount + 2),
                initialUserCount + 2, userDatabase.getNextID());
        userDatabase.insertUser(user2);
        assertEquals("The ID of the second event inserted should be " + (initialUserCount + 2),
                initialUserCount + 2, user2.getID());

        userDatabase.insertUser(user3);
        userDatabase.insertUser(user3); // should throw an exception

        assertEquals("The next ID after three events, with one duplicate attempt should be " + (initialUserCount + 4),
                initialUserCount + 4, userDatabase.getNextID());

        userDatabase.deleteUser(user2);
        assertNotEquals("The next ID after a deletion should not be the deleted ID.",
                user2.getID(), userDatabase.getNextID());
        assertEquals("The next ID should be 4", initialUserCount + 4, userDatabase.getNextID());
    }

    @Test
    public void testGetAllEvents() {
        user1 = userDatabase.insertUser(user1);
        user1 = userDatabase.addUserEvent(user1, event1);
        user1 = userDatabase.addUserEvent(user1, event2);
        List<EventDSO> events = userDatabase.getAllEvents(user1);
        assertEquals("The user should have 2 events", 2, events.size());
        assertTrue("The user should contain event1", events.contains(event1));
        assertTrue("The user should contain event2", events.contains(event2));

        userDatabase.removeUserEvent(user1, event1);
        events = userDatabase.getAllEvents(user1);
        assertEquals("The user should have 1 event", 1, events.size());
        assertFalse("The user should not contain event1",
                events.contains(event1));
        assertTrue("The user should contain event2", events.contains(event2));
    }

    @Test
    public void testGetAllLabels() {
        user1 = userDatabase.insertUser(user1);

        assertEquals("The user should not have any labels to start",
                0, userDatabase.getAllLabels(user1).size());

        userDatabase.addUserLabel(user1, label1);
        userDatabase.addUserLabel(user1, label2);

        List<EventLabelDSO> result = userDatabase.getAllLabels(user1);
        assertEquals("The user should now have 2 labels",
                2, result.size());
        assertTrue(result.contains(label1));
        assertTrue(result.contains(label2));

        UserDSO testUser = new UserDSO(initialUserCount + 4, "email2",
                Calendar.getInstance(), "1234");
        userDatabase.insertUser(testUser);
        userDatabase.addUserLabel(testUser, label3);

        result = userDatabase.getAllLabels(user1);
        assertEquals("The user should only have their own labels", 2, result.size());
        assertTrue(result.contains(label1));
        assertTrue(result.contains(label2));
        assertFalse(result.contains(label3));
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetAllLabelsException() {
        userDatabase.getAllLabels(user3);
    }

    @Test
    public void testGetFavorites() {
        user1 = userDatabase.insertUser(user1);

        assertEquals("User should have no favorites to start",
                0, userDatabase.getFavorites(user1).size());

        user1 = userDatabase.addUserFavorite(user1, event1);

        assertEquals("User should now have 1 favorite",
                1, userDatabase.getFavorites(user1).size());
        assertTrue("User should have event1 as a favorite",
                userDatabase.getFavorites(user1).contains(event1));

        user1 = userDatabase.addUserEvent(user1, event2);

        assertEquals("Adding an event should not add as a favorite",
                1, userDatabase.getFavorites(user1).size());

        user1 = userDatabase.addUserFavorite(user1, event2);

        assertEquals("User should now have 2 favorites",
                2, userDatabase.getFavorites(user1).size());
        assertTrue("User should have event2 as a favorite",
                userDatabase.getFavorites(user1).contains(event2));

        user1 = userDatabase.removeUserEvent(user1, event1);

        assertEquals("Removing an event should remove it as a favorite",
                1, userDatabase.getFavorites(user1).size());

        user1 = userDatabase.removeUserFavorite(user1, event2);

        assertFalse("Event 2 should no longer be a favorite",
                userDatabase.getFavorites(user1).contains(event2));
        assertEquals("User should have no favorites",
                0, userDatabase.getFavorites(user1).size());
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetFavoritesException() {
        userDatabase.getFavorites(user3);
    }

    @Test
    public void testSetEventStatus() {
        user1 = userDatabase.insertUser(user1);
        user1 = userDatabase.addUserEvent(user1, event1);
        user1 = userDatabase.addUserEvent(user1, event2);

        EventDSO event1 = userDatabase.getAllEvents(user1).get(0);
        EventDSO event2 = userDatabase.getAllEvents(user1).get(1);

        assertFalse("event1 should not be complete", event1.isDone());
        assertFalse("event2 should not be complete", event2.isDone());

        user1 = userDatabase.setEventStatus(user1, event1, true);
        assertTrue("event1 should now be complete", event1.isDone());
        assertFalse("event2 should still not be complete", event2.isDone());

        user1 = userDatabase.setEventStatus(user1, event1, false);
        user1 = userDatabase.setEventStatus(user1, event2, false);
        assertFalse("event1 should be back to not be complete", event1.isDone());
        assertFalse("event2 should be set as not complete", event2.isDone());
    }

    @Test (expected = UserNotFoundException.class)
    public void testSetEventStatusUserException() {
        userDatabase.setEventStatus(user3, event1, true);
    }

    @Test (expected = EventNotFoundException.class)
    public void testSetEventStatusEventException() {
        user1 = userDatabase.insertUser(user1);
        userDatabase.setEventStatus(user1, event1, true);
    }


    @Test
    public void testAddUserEvent() {
        user1 = userDatabase.insertUser(user1);

        assertEquals("User should no events to start", 0, user1.getUserEvents().size());

        user1 = userDatabase.addUserEvent(user1, event1);
        user1 = userDatabase.addUserEvent(user1, event2);

        assertEquals("User should 2 events", 2, user1.getUserEvents().size());
        assertTrue("User should have event1", user1.getUserEvents().contains(event1));
        assertTrue("User should have event2", user1.getUserEvents().contains(event2));
    }

    @Test (expected = UserNotFoundException.class)
    public void testAddUserEventUserException() {
        userDatabase.addUserEvent(user1, event1);
    }

    @Ignore
    @Test (expected = EventNotFoundException.class)
    public void testAddUserEventEventException() {
        user1 = userDatabase.insertUser(user1);
        userDatabase.addUserEvent(user1, event1);
    }

    @Test
    public void testAddUserLabel() {
        user1 = userDatabase.insertUser(user1);

        assertEquals("The user should not have any labels to start",
                0, userDatabase.getAllLabels(user1).size());

        UserDSO result = userDatabase.addUserLabel(user1, label1);
        assertNotNull("The user should not be null after adding label1", result);
        result = userDatabase.getUserByEmail("uid1");
        assertNotNull("The user should be found in the database", result);
        assertEquals("The user should have 1 label", 1, result.getUserLabels().size());
        assertTrue(result.getUserLabels().contains(label1));

        result = userDatabase.addUserLabel(user1, label2);
        assertNotNull("The user should not be null after adding label2", result);
        result = userDatabase.getUserByEmail("uid1");
        assertNotNull("The user should be found in the database", result);
        assertEquals("The user should have 2 label", 2, result.getUserLabels().size());
        assertTrue(result.getUserLabels().contains(label2));
    }

    @Test
    public void testAddUserFavorite() {
        user1 = userDatabase.insertUser(user1);

        user1 = userDatabase.addUserFavorite(user1, event1);
        assertNotNull("User should not be null after adding event1 as " + "favorite", user1);
        user1 = userDatabase.addUserFavorite(user1, event2);
        assertNotNull("User should not be null after adding event2 as " + "favorite", user1);
        user1 = userDatabase.getUserByEmail("uid1");
        assertEquals("User should have 2 favorites",
                2, userDatabase.getFavorites(user1).size());
    }

    @Test
    public void testRemoveUserEvent() {
        user1 = userDatabase.insertUser(user1);

        user1 = userDatabase.addUserEvent(user1, event1);
        user1 = userDatabase.addUserEvent(user1, event2);

        user1 = userDatabase.removeUserEvent(user1, event1);
        assertEquals("User should 1 event", 1, user1.getUserEvents().size());
        assertTrue("User should have event2", user1.getUserEvents().contains(event2));
        assertFalse("User should not have removed event1", user1.getUserEvents().contains(event1));
    }

    @Test
    public void testRemoveUserLabel() {
        user1 = userDatabase.insertUser(user1);

        user1 = userDatabase.addUserLabel(user1, label1);
        user1 = userDatabase.addUserLabel(user1, label2);

        user1 = userDatabase.removeUserLabel(user1, label1);
        assertEquals("The user should now have 1 label", 1, user1.getUserLabels().size());
        assertTrue("The user should still contain label2", user1.getUserLabels().contains(label2));
        assertFalse("The user should not contain the removed label1", user1.getUserLabels().contains(label1));
    }

    @Test
    public void testRemoveUserFavorite() {
        user1 = userDatabase.insertUser(user1);

        user1 = userDatabase.addUserFavorite(user1, event1);
        user1 = userDatabase.addUserFavorite(user1, event2);

        user1 = userDatabase.removeUserFavorite(user1, event1);
        assertEquals("User should still have 2 events", 2, user1.getUserEvents().size());
        assertEquals("User should have 1 favorite",
                1, userDatabase.getFavorites(user1).size());
        assertTrue("User should have event2 as a favorite", user1.getUserFavorites().contains(event2));
        assertFalse("User should not have removed event1 as a favorite", user1.getUserFavorites().contains(event1));
    }

    @Test (expected = UserNotFoundException.class)
    public void testUserNotFoundException() {
        userDatabase.getUserByID(user1.getID());
        userDatabase.getUserByEmail(user1.getEmail());
        userDatabase.insertUser(user1);
        userDatabase.updateUserName(user1, "badTest");
        userDatabase.updateUserEmail(user1, "badTest");
        userDatabase.updateUserPassword(user1, "badTest");
        userDatabase.deleteUser(user1);
        userDatabase.getAllEvents(user1);
        userDatabase.getAllLabels(user1);
        userDatabase.getFavorites(user1);
        userDatabase.setEventStatus(user1, event1, true);
        userDatabase.addUserEvent(user1, event1);
        userDatabase.removeUserEvent(user1, event1);
        userDatabase.addUserLabel(user1, label1);
        userDatabase.removeUserLabel(user1, label1);
        userDatabase.addUserFavorite(user1, event1);
        userDatabase.removeUserFavorite(user1, event1);
    }

    @Ignore
    @Test (expected = EventNotFoundException.class)
    public void testEventNotFoundException() {
        // TODO
    }

    @Ignore
    @Test (expected = EventLabelNotFoundException.class)
    public void testEventLabelNotFoundException() {
        // TODO
    }


}
