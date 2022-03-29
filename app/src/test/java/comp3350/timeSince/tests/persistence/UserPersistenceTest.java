package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.fakes.UserPersistence;

public class UserPersistenceTest {

    private IUserPersistence userDatabase;
    private UserDSO user1, user2, user3;
    private List<UserDSO> userList;
    private Calendar defaultDate;

    @Rule
    public ExpectedException exceptionRule;

    @Before
    public void setUp() {
        userDatabase = new UserPersistence();
        defaultDate = Calendar.getInstance();

        user1 = new UserDSO("uid1", defaultDate, "hash1");
        user2 = new UserDSO("uid2", defaultDate, "hash2");
        user3 = new UserDSO("uid3", defaultDate, "hash3");
        userList = new ArrayList<>(Arrays.asList(user1, user2, user3));
    }

    @After
    public void tearDown() {
        List<UserDSO> tempList = userDatabase.getUserList();
        if (tempList.contains(user1)) {
            userDatabase.deleteUser(user1);
        }
        if (tempList.contains(user2)) {
            userDatabase.deleteUser(user2);
        }
        if (tempList.contains(user3)) {
            userDatabase.deleteUser(user3);
        }
    }

    @Test
    public void testGetUserList() {
        assertNotNull("Newly created database object should not be null",
                userDatabase);
        assertEquals("Newly created database should have no users",
                0, userDatabase.numUsers());
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);
        List<UserDSO> actual = userDatabase.getUserList();
        assertEquals("Size of database should be 3", 3, actual.size());
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
        UserDSO actual = userDatabase.getUserByID("uid1");
        assertEquals("The correct user should be returned if present",
                user1, actual);
        assertNull("Null should be returned if user is not present",
                userDatabase.getUserByID("uid4"));
    }

    @Test
    public void testInsertUser() {
        assertEquals("Size of database should be 0",
                0, userDatabase.numUsers());
        assertNotNull(userDatabase.insertUser(user1));
        assertEquals("Size of database should be 1",
                1, userDatabase.numUsers());

        userDatabase.insertUser(user2);
        assertEquals("Size of database should be 2",
                2, userDatabase.numUsers());

        userDatabase.insertUser(user3);
        assertEquals("Size of database should be 3",
                3, userDatabase.numUsers());
    }

    @Test(expected = DuplicateUserException.class)
    public void testInsertUserException() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user1);
        assertEquals("Should not be able to insert a duplicate user",
                1, userDatabase.numUsers());
    }

    @Test
    public void testUpdateUser() {
        userDatabase.insertUser(user1);
        assertEquals("Size of database should be 1", 1,
                userDatabase.numUsers());

        user1.setName("hello");
        userDatabase.updateUser(user1);
        assertEquals("New attributes should match", "hello",
                userDatabase.getUserByID("uid1").getName());

        userDatabase.insertUser(user3);
        user3.setMembershipType(UserDSO.MembershipType.paid);
        assertNotEquals("Old attributes should be changed",
                UserDSO.MembershipType.free,
                userDatabase.getUserByID("uid3").getMembershipType());
    }

    @Test
    public void testDeleteUser() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);

        assertEquals("Size of database should be 3", 3,
                userDatabase.numUsers());

        assertEquals("If user exists, return the user that was deleted",
                user1, userDatabase.deleteUser(user1));

        assertEquals("Size of database should be 2", 2,
                userDatabase.numUsers());

        assertNull("Deleted user should no longer be in database",
                userDatabase.getUserByID(user1.getID()));
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUserException() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);
        userDatabase.deleteUser(new UserDSO("uid4", defaultDate, "password"));
        assertEquals("Size of database should be 3", 3, userDatabase.numUsers());
    }

    @Test
    public void testIsUnique() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        assertTrue("A unique user should be considered unique",
                userDatabase.isUnique(user3.getID()));
        assertFalse("User should not be unique if one already exists",
                userDatabase.isUnique(user1.getID()));
    }
}
