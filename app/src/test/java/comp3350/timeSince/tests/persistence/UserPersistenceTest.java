package comp3350.timeSince.tests.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.hsqldb.UserPersistenceHSQLDB;

public class UserPersistenceTest {

    private IUserPersistence userDatabase;
    private UserDSO user1, user2, user3;
    private List<UserDSO> userList;

    @Before
    public void setUp() {
        userDatabase = Services.getUserPersistence();
//        user1 = new UserDSO("uid1", "hash1");
//        user2 = new UserDSO("uid2", "hash2");
//        user3 = new UserDSO("uid3", "hash3");
//        userList = new ArrayList<>();
//        userList.add(user2);
//        userList.add(user3);
//        userList.add(user1);
    }

    @After
    public void tearDown() {
        //userDatabase.deleteUser(user1);
        //userDatabase.deleteUser(user2);
        //userDatabase.deleteUser(user3);
        //userDatabase = null;
    }

    @Test
    public void testTest() {

    }

    @Test
    public void testGetUserList() {
        assertNotNull("Newly created database object should not be null",
                userDatabase);
        assertEquals("Newly created database should have no users",
                userDatabase.numUsers(), 0);
        userDatabase.insertUser(user1);
        userDatabase.insertUser(user2);
        userDatabase.insertUser(user3);
        List<UserDSO> actual = userDatabase.getUserList();
        assertTrue("Database should contain user1", actual.contains(user1));
        assertTrue("Database should contain user2", actual.contains(user2));
        assertTrue("Database should contain user3", actual.contains(user3));
        assertTrue("Database should have all existing users",
                actual.containsAll(userList));
        assertTrue("Database should have all existing users",
                userList.containsAll(actual));
        assertFalse("Database should not contain a user that does not exist",
                actual.contains(new UserDSO("uid4", "password")));
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
//        assertEquals("Size of database should be 0",
//                userDatabase.numUsers(), 0);
        assertNotNull(userDatabase.insertUser(user1));
//        assertEquals("Size of database should be 1",
//                1, userDatabase.numUsers());
//
//        userDatabase.insertUser(user2);
//        assertEquals("Size of database should be 2",
//                2, userDatabase.numUsers());
//
//        userDatabase.insertUser(user3);
//        assertEquals("Size of database should be 3",
//                3, userDatabase.numUsers());
//
//        userDatabase.insertUser(user1);
//        assertEquals("Size of database should be 3",
//                3, userDatabase.numUsers());
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

        userDatabase.deleteUser(user2);
        assertEquals("Size of database should be 2", 2,
                userDatabase.numUsers());

        assertNull("Deleted user should no longer be in database",
                userDatabase.getUserByID(user2.getID()));

        assertEquals("If user exists, return the user that was deleted",
                user1, userDatabase.deleteUser(user1));

        assertNull("Shouldn't be able to delete a user that doesn't exist",
                userDatabase.deleteUser(new UserDSO("uid4", "password")));

        assertEquals("Size of database should be 1", 1, userDatabase.numUsers());

        userDatabase.deleteUser(user3);
        assertEquals("Size of database should be 0", 0, userDatabase.numUsers());
        assertNull("Should return null when database is empty",
                userDatabase.deleteUser(user1));
    }
}
