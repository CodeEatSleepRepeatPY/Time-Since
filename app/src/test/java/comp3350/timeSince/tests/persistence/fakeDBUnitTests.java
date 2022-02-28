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

public class fakeDBUnitTests {

    @Test
    public void createDatabaseTest(){
        FakeDatabase database = new FakeDatabase();
        assertNotNull("Newly created database object should not be null", database);
        assertEquals("The database should be empty", database.getUsers().size(), 0);
    }

    @Test
    public void emptyDatabaseTest(){
        FakeDatabase database = new FakeDatabase();
        assertEquals("The database should be empty", database.getUsers().size(), 0);
    }

    @Test
    public void addUserTest(){
        FakeDatabase database = new FakeDatabase();
        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        assertEquals("Size of database should be 3", database.getUsers().size(), 3);
        assertEquals("Name of first user should be guy1", database.getUser("uid1").getName(), "guy1");
        assertEquals("Name of second user should be guy2", database.getUser("uid2").getName(), "guy2");
        assertEquals("Name of third user should be guy3", database.getUser("uid3").getName(), "guy3");
    }

    @Test
    public void removeUserTest(){
        FakeDatabase database = new FakeDatabase();
        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        database.removeUser("uid1");
        assertEquals("Size of database should be 2", database.getUsers().size(), 2);
        assertNull("user1 should be null", database.getUser("uid1"));

        database.removeUser("uid2");
        assertEquals("Size of database should be 1", database.getUsers().size(), 1);
        assertNull("user2 should be null", database.getUser("uid2"));

        database.removeUser("uid3");
        assertEquals("Size of database should be 0", database.getUsers().size(), 0);
        assertNull("user3 should be null", database.getUser("uid3"));
    }

    @Test
    public void addEventTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        assertEquals("event1 should have the name 'event1'", database.getUserEvents(user1).get(0).getName(), "event1");
        assertEquals("event2 should have the name 'event2'", database.getUserEvents(user2).get(0).getName(), "event2");
        assertEquals("event3 should have the name 'event3'", database.getUserEvents(user3).get(0).getName(), "event3");
    }

    @Test
    public void removeEventTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.addEvent(user1, event1);
        database.addEvent(user2, event2);
        database.addEvent(user3, event3);

        database.removeEvent(user1, event1);
        database.removeEvent(user3, event3);

        assertEquals("user1 should have 0 events", database.getUserEvents(user1).size(), 0);
        assertEquals("user2 should still have 1 event", database.getUserEvents(user2).size(), 1);
        assertEquals("user3 should have 0 events", database.getUserEvents(user3).size(), 0);
    }

    @Test
    public void getUsersTest(){
        FakeDatabase database = new FakeDatabase();

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        assertEquals("getUsers() should return 3", database.getUsers().size(), 3);
    }

    @Test
    public void getUserEventsTest(){
        FakeDatabase database = new FakeDatabase();
        Date currentDateTime = new Date(System.currentTimeMillis());

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        database.addUser(user1);

        EventDSO event1 = new EventDSO("event1", currentDateTime);
        EventDSO event2 = new EventDSO("event2", currentDateTime);
        EventDSO event3 = new EventDSO("event3", currentDateTime);

        database.addEvent(user1, event1);
        database.addEvent(user1, event2);
        database.addEvent(user1, event3);

        assertEquals("user1 should have 3 events", database.getUserEvents(user1).size(), 3);
        assertEquals("first event should be called event1", database.getUserEvents(user1).get(0).getName(), "event1");
        assertEquals("first event should be called event2", database.getUserEvents(user1).get(1).getName(), "event2");
        assertEquals("first event should be called event3", database.getUserEvents(user1).get(2).getName(), "event3");
    }

    @Test
    public void getUserTest(){
        FakeDatabase database = new FakeDatabase();

        UserDSO user1 = new UserDSO("guy1", UserDSO.MembershipType.free, "uid1", "date1", "hash1");
        UserDSO user2 = new UserDSO("guy2", UserDSO.MembershipType.free, "uid2", "date2", "hash2");
        UserDSO user3 = new UserDSO("guy3", UserDSO.MembershipType.free, "uid3", "date3", "hash3");

        assertNull("database should not contain any users", database.getUser("uid1"));
        assertNull("database should not contain any users", database.getUser("uid2"));
        assertNull("database should not contain any users", database.getUser("uid3"));

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);

        assertEquals("user1 should have uid1", database.getUser("uid1").getName(), "guy1");
        assertEquals("user2 should have uid2", database.getUser("uid2").getName(), "guy2");
        assertEquals("user3 should have uid3", database.getUser("uid3").getName(), "guy3");
    }
}
