package comp3350.timeSince.tests.persistence;

import comp3350.timeSince.persistence.FakeDatabase;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.persistence.I_Database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FakeDBUnitTests {

    private I_Database database;
    private UserDSO user1, user2, user3;
    private EventDSO event1, event2, event3;

    @Before
    public void setup(){
        database = new FakeDatabase();

        user1 = new UserDSO("uid1", UserDSO.MembershipType.free, "hash1");
        user2 = new UserDSO("uid2", UserDSO.MembershipType.free, "hash2");
        user3 = new UserDSO("uid3", UserDSO.MembershipType.free, "hash3");

        event1 = new EventDSO("event1");
        event2 = new EventDSO("event2");
        event3 = new EventDSO("event3");
    }

    @Test
    public void createDatabaseTest(){
        assertNotNull("Newly created database object should not be null", database);
        assertEquals("The database should be empty",  0, database.getUsers().size());
    }

    @Test
    public void emptyDatabaseTest(){
        assertEquals("The database should be empty", 0, database.getUsers().size());
    }

    @Test
    public void addUserTest(){
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        assertEquals("Size of database should be 3", 3, database.getUsers().size());
    }

    @Test
    public void removeUserTest(){
        database.removeUser(user1);
        assertEquals("Size of database should be 0", 0, database.getUsers().size());

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        database.removeUser(user1);
        assertEquals("Size of database should be 2", 2, database.getUsers().size());
        assertNull("user1 should be null", database.getUser("uid1"));

        database.removeUser(user2);
        assertEquals("Size of database should be 1", 1, database.getUsers().size());
        assertNull("user2 should be null", database.getUser("uid2"));

        database.removeUser(user3);
        assertEquals("Size of database should be 0", 0, database.getUsers().size());
        assertNull("user3 should be null", database.getUser("uid3"));
    }

    @Test
    public void addEventTest(){
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        assertEquals("user1 should have 1 event", 1, user1.getUserEvents().size());
        assertEquals("user2 should have 1 event", 1, user2.getUserEvents().size());
        assertEquals("user3 should have 1 event", 1, user3.getUserEvents().size());

        assertEquals("event1 object should have the name 'event1'", "event1", user1.getUserEvents().get(0).getName());
        assertEquals("event2 object should have the name 'event2'", "event2", user2.getUserEvents().get(0).getName());
        assertEquals("event3 object should have the name 'event3'", "event3", user3.getUserEvents().get(0).getName());
    }

    @Test
    public void removeEventTest(){
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        database.removeEvent(user1, event1);
        assertEquals("user1 should have 0 events", 0, user1.getUserEvents().size());

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        database.removeEvent(user1, event1);
        database.removeEvent(user3, event3);

        assertEquals("user1 should have 0 events", 0, user1.getUserEvents().size());
        assertEquals("user2 should still have 1 event", 1, user2.getUserEvents().size());
        assertEquals("user3 should have 0 events", 0, user3.getUserEvents().size());

        database.removeEvent(user2, event2);
        assertEquals("user2 should have 0 events", 0, user2.getUserEvents().size());

        database.removeEvent(user2, event2);
        assertEquals("user2 should still have 0 events", 0, user2.getUserEvents().size());
    }

    @Test
    public void getUsersTest(){
        database.addUser(user1);
        assertEquals("getUsers().size() should return 1", 1, database.getUsers().size());

        database.addUser(user2);
        assertEquals("getUsers().size() should return 2", 2, database.getUsers().size());

        database.addUser(user3);
        assertEquals("getUsers().size() should return 3", 3, database.getUsers().size());
    }

    @Test
    public void getUserTest(){
        assertNull("database should not contain any users", database.getUser("uid1"));
        assertNull("database should not contain any users", database.getUser("uid2"));
        assertNull("database should not contain any users", database.getUser("uid3"));

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        assertNotNull("user1, with uid1, should be in the database", database.getUser("uid1"));
        assertNotNull("user2, with uid2, should be in the database", database.getUser("uid2"));
        assertNotNull("user3, with uid3, should be in the database", database.getUser("uid3"));

        database.removeUser(user1);
        assertNull("database should not contain user1 so null should be returned", database.getUser(user1.getUuid()));
    }
}
