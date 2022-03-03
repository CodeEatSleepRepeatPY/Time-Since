package comp3350.timeSince.tests.persistence;

import comp3350.timeSince.persistence.FakeDatabase;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FakeDBUnitTests {

    FakeDatabase database;
    UserDSO user1;
    UserDSO user2;
    UserDSO user3;

    @Before
    public void setup(){
        database = new FakeDatabase();

        user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");
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
        assertEquals("Name of first user should be guy1", "guy1", database.getUser("uid1").getName());
        assertEquals("Name of second user should be guy2", "guy2", database.getUser("uid2").getName());
        assertEquals("Name of third user should be guy3", "guy3", database.getUser("uid3").getName());
    }

    @Test
    public void removeUserTest(){
        database.removeUser(user1);
        assertEquals("Size of database should be 0", 0, database.getUsers().size());

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1");
        EventDSO event2 = new EventDSO("event2");
        EventDSO event3 = new EventDSO("event3");

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

        EventDSO event1 = new EventDSO("event1");
        EventDSO event2 = new EventDSO("event2");
        EventDSO event3 = new EventDSO("event3");

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

        EventDSO event1 = new EventDSO("event1");
        EventDSO event2 = new EventDSO("event2");
        EventDSO event3 = new EventDSO("event3");

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

        assertEquals("user1 should have uid1", "guy1", database.getUser("uid1").getName());
        assertEquals("user2 should have uid2", "guy2", database.getUser("uid2").getName());
        assertEquals("user3 should have uid3", "guy3", database.getUser("uid3").getName());

        database.removeUser(user1);
        assertNull("database should not contain user1 so null should be returned", database.getUser(user1.getUuid()));
    }
}
