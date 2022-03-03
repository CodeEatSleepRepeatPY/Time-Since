package comp3350.timeSince.tests.persistence;

import comp3350.timeSince.persistence.FakeDatabase;
import comp3350.timeSince.persistence.I_Database;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

import static java.lang.String.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FakeDBUnitTests {

    @Test
    public void createDatabaseTest(){
        FakeDatabase database = new FakeDatabase();
        assertNotNull("Newly created database object should not be null", database);
        assertEquals("The database should be empty",  0, database.getUsers().size());
    }

    @Test
    public void emptyDatabaseTest(){
        FakeDatabase database = new FakeDatabase();
        assertEquals("The database should be empty", 0, database.getUsers().size());
    }

    @Test
    public void addUserTest(){
        FakeDatabase database = new FakeDatabase();
        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

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
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

        database.removeUser(user1);
        assertEquals("Size of database should be 0", 0, database.getUsers().size());

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        database.removeUser(user1);
        assertEquals("Size of database should be 2", 2, database.getUsers().size());
        assertNull("user1 should be null", database.getUser("uid1"));

        database.removeUser(user2);
        assertEquals("Size of database should be 1", 1, database.getUsers().size());
        assertNull("user2 should be null", database.getUser("uid2"));
        assertEquals("user2 shouldn't have any events", 0, database.getUserEvents(user2).size());

        database.removeUser(user3);
        assertEquals("Size of database should be 0", 0, database.getUsers().size());
        assertNull("user3 should be null", database.getUser("uid3"));
        assertEquals("user3 shouldn't have any events", 0, database.getUserEvents(user3).size());
    }

    @Test
    public void addEventTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        assertEquals("user1 should have 1 event", 1, database.getUserEvents(user1).size());
        assertEquals("user2 should have 1 event", 1, database.getUserEvents(user2).size());
        assertEquals("user3 should have 1 event", 1, database.getUserEvents(user3).size());

        assertEquals("event1 object should have the name 'event1'", "event1", database.getUserEvents(user1).get(0).getName());
        assertEquals("event2 object should have the name 'event2'", "event2", database.getUserEvents(user2).get(0).getName());
        assertEquals("event3 object should have the name 'event3'", "event3", database.getUserEvents(user3).get(0).getName());
    }

    @Test
    public void removeEventTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.removeEvent(user1, event1);
        assertEquals("user1 should have 0 events", 0, database.getUserEvents(user1).size());

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        database.removeEvent(user1, event1);
        database.removeEvent(user3, event3);

        assertEquals("user1 should have 0 events", 0, database.getUserEvents(user1).size());
        assertEquals("user2 should still have 1 event", 1, database.getUserEvents(user2).size());
        assertEquals("user3 should have 0 events", 0, database.getUserEvents(user3).size());

        database.removeEvent(user2, event2);
        assertEquals("user2 should have 0 events", 0, database.getUserEvents(user2).size());

        database.removeEvent(user2, event2);
        assertEquals("user2 should still have 0 events", 0, database.getUserEvents(user2).size());
    }

    @Test
    public void getUsersTest(){
        FakeDatabase database = new FakeDatabase();

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

        database.addUser(user1);
        assertEquals("getUsers().size() should return 1", 1, database.getUsers().size());

        database.addUser(user2);
        assertEquals("getUsers().size() should return 2", 2, database.getUsers().size());

        database.addUser(user3);
        assertEquals("getUsers().size() should return 3", 3, database.getUsers().size());
    }

    @Test
    public void getUserEventsTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        database.addUser(user1);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        assertEquals("user1 should have 0 events", 0, database.getUserEvents(user1).size());

        database.addEvent(user1, event1);
        database.addEvent(user1, event2);
        database.addEvent(user1, event3);

        assertEquals("user1 should have 3 events", 3, database.getUserEvents(user1).size());
        assertEquals("first event should be called event1", "event1", database.getUserEvents(user1).get(0).getName());
        assertEquals("first event should be called event2", "event2", database.getUserEvents(user1).get(1).getName());
        assertEquals("first event should be called event3", "event3", database.getUserEvents(user1).get(2).getName());

        database.removeEvent(user1, event1);
        assertEquals("user1 should only have 2 events", 2, database.getUserEvents(user1).size());
    }

    @Test
    public void getUserTest(){
        FakeDatabase database = new FakeDatabase();

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "hash3");

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
