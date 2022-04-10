package comp3350.timeSince.tests.persistence;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class UserPersistenceHSQLDBTest {

    private IUserPersistence userDatabase;
    private UserDSO user1, user2, user3;
    private List<UserDSO> userList;
    private Calendar defaultDate;
    private static final int initialUserCount = 2;

    @Rule
    public ExpectedException exceptionRule;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();
        userDatabase = Services.getUserPersistence(true);
        defaultDate = Calendar.getInstance();
        user1 = new UserDSO(initialUserCount + 1, "uid1", defaultDate, "hash1");
        user2 = new UserDSO(initialUserCount + 2, "uid2", defaultDate, "hash2");
        user3 = new UserDSO(initialUserCount + 3, "uid3", defaultDate, "hash3");
        userList = new ArrayList<>(Arrays.asList(user1, user2, user3));
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
        assertNotNull("The returned list should not be null", actual);
        assertEquals("Size of database should be " + (initialUserCount + 3),
                initialUserCount + 3, actual.size());
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
        UserDSO actual = userDatabase.getUserByEmail("uid1");
        assertEquals("The correct user should be returned if present",
                user1, actual);
    }

    @Test (expected = UserNotFoundException.class)
    public void testGetUserByIDException() {
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
        assertEquals("Size of database should be " + (initialUserCount + 2), initialUserCount + 1,
                userDatabase.numUsers());

        user1.setName("hello");
        userDatabase.updateUserName(user1);
        assertEquals("New attributes should match", "hello",
                userDatabase.getUserByEmail("uid1").getName());
    }

    @Test (expected = UserNotFoundException.class)
    public void testUpdateUserException() {
        userDatabase.updateUser(user1); // should not be able to update user not in db
    }

    @Test
    public void testDeleteUser() {
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);

        assertEquals("Size of database should be " + (initialUserCount + 3),
                initialUserCount + 3, userDatabase.numUsers());

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
        assertEquals("Size of database should be " + (initialUserCount + 3),
                initialUserCount + 3, userDatabase.numUsers());
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

}
