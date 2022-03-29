package comp3350.timeSince.tests.objects;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;

public class UserDSOTest {
    private UserDSO userDSO;
    private UserDSO.MembershipType membershipType;
    private String id;
    private String passwordHash;

    @Before
    public void setUp() {
        this.id = "bobby_g@gmail.com";
        this.membershipType = UserDSO.MembershipType.free;
        this.passwordHash = "p4ssw0rd";

        this.userDSO = new UserDSO(id, passwordHash);
        this.userDSO.setMembershipType(membershipType);
    }

    @Test
    public void testGetName() {
        String message = String.format("Initial name should be set to %s",
                this.id);

        Assert.assertEquals(message, this.id, this.userDSO.getName());
    }

    @Test
    public void testGetMembershipType() {
        String message = String.format("Initial membership type should be set" +
                "to %s", this.membershipType.name());

        Assert.assertEquals(message, this.membershipType,
                this.userDSO.getMembershipType());
    }

    @Test
    public void testGetID() {
        String message = String.format("Initial uuid should be set to %s",
                this.id);

        Assert.assertEquals(message, this.id, this.userDSO.getID());
    }

    @Test
    public void testGetDateRegistered() {
        int wiggleRoom = 10;
        Date slightPast = new Date(System.currentTimeMillis() - wiggleRoom);
        Date slightFuture = new Date(System.currentTimeMillis() + wiggleRoom);
        Date dateRegistered = this.userDSO.getDateRegistered();
        String message = String.format("Expected the date registered to be " +
                "in the range %s < date registered < %s ", slightPast,
                slightFuture);

        Assert.assertTrue(message, dateRegistered.after(slightPast) &&
                dateRegistered.before(slightFuture));
    }

    @Test
    public void testGetPasswordHash() {
        String message = String.format("Initial password hash should be set" +
                        "to %s", this.passwordHash);

        Assert.assertEquals(message, this.passwordHash,
                this.userDSO.getPasswordHash());
    }

    @Test
    public void testGetUserLabels() {
        String message = "Initial user labels should be empty";
        int size = 0;

        Assert.assertEquals(message, size,
                this.userDSO.getUserLabels().size());
    }

    @Test
    public void testGetUserEvents() {
        String message = "Initial user events should be empty";
        int size = 0;

        Assert.assertEquals(message, size,
                this.userDSO.getUserEvents().size());
    }

    @Test
    public void testGetFavoritesList() {
        String message = "Initial favorites list should be empty";
        int size = 0;

        Assert.assertEquals(message, size,
                this.userDSO.getFavoritesList().size());
    }

    @Test
    public void testSetName() {
        String newName = "Gary";
        String message = String.format("The name should now be set to %s",
                newName);
        this.userDSO.setName(newName);

        Assert.assertEquals(message, newName, this.userDSO.getName());
    }

    @Test
    public void testSetMembershipType() {
        UserDSO.MembershipType newMembership = UserDSO.MembershipType.paid;
        String message = String.format("The membership status should now be" +
                        "set to %s", newMembership.name());
        this.userDSO.setMembershipType(newMembership);

        Assert.assertEquals(message, newMembership,
                this.userDSO.getMembershipType());
    }

    @Test
    public void testSetUuid() {
        String newUuid = "cheese@gmail.com";
        String message = String.format("The user's uuid should now be" +
                "set to %s", newUuid);
        this.userDSO.setID(newUuid);

        Assert.assertEquals(message, newUuid, this.userDSO.getID());
    }

    @Test
    public void testSetPasswordHash() {
        String newPasswordHash = "11111";
        String message = String.format("The user's password hash should now " +
                "be set to %s", newPasswordHash);
        this.userDSO.setPasswordHash(newPasswordHash);

        Assert.assertEquals(message, newPasswordHash,
                this.userDSO.getPasswordHash());
    }

    @Test
    public void testAddEvent(){
        EventDSO event1 = new EventDSO("event1");
        EventDSO event2 = new EventDSO("event2");
        EventDSO event3 = new EventDSO("event3");

        Assert.assertEquals("size of userEvents list should be 0", 0, userDSO.getUserEvents().size());
        userDSO.addEvent(event1);
        Assert.assertEquals("size of userEvents list should be 1", 1, userDSO.getUserEvents().size());
        Assert.assertEquals("name of first event in the list should be event1", "event1", userDSO.getUserEvents().get(0).getName());
        userDSO.addEvent(event2);
        Assert.assertEquals("size of userEvents list should be 2", 2, userDSO.getUserEvents().size());
        Assert.assertEquals("name of second event in the list should be event2", "event2", userDSO.getUserEvents().get(1).getName());
        userDSO.addEvent(event3);
        Assert.assertEquals("size of userEvents list should be 3", 3, userDSO.getUserEvents().size());
        Assert.assertEquals("name of third event in the list should be event3", "event3", userDSO.getUserEvents().get(2).getName());
        userDSO.addEvent(event1);
        userDSO.addEvent(event2);
        userDSO.addEvent(event3);
        Assert.assertEquals("size of userEvents list should still be 3 (shouldn't add duplicates)", 3, userDSO.getUserEvents().size());
    }

    @Test
    public void testAddEventLabel(){
        EventLabelDSO eventLabel1 = new EventLabelDSO("eventLabel1");
        EventLabelDSO eventLabel2 = new EventLabelDSO("eventLabel2");
        EventLabelDSO eventLabel3 = new EventLabelDSO("eventLabel3");

        Assert.assertEquals("size of userLabels list should be 0", 0, userDSO.getUserLabels().size());
        userDSO.addEventLabel(eventLabel1);
        Assert.assertEquals("size of userLabels list should be 1", 1, userDSO.getUserLabels().size());
        Assert.assertEquals("name of first event label in the list should be eventLabel1", "eventLabel1", userDSO.getUserLabels().get(0).getName());
        userDSO.addEventLabel(eventLabel2);
        Assert.assertEquals("size of userLabels list should be 2", 2, userDSO.getUserLabels().size());
        Assert.assertEquals("name of second event label in the list should be eventLabel2", "eventLabel2", userDSO.getUserLabels().get(1).getName());
        userDSO.addEventLabel(eventLabel3);
        Assert.assertEquals("size of userLabels list should be 3", 3, userDSO.getUserLabels().size());
        Assert.assertEquals("name of third event label in the list should be eventLabel3", "eventLabel3", userDSO.getUserLabels().get(2).getName());
        userDSO.addEventLabel(eventLabel1);
        userDSO.addEventLabel(eventLabel2);
        userDSO.addEventLabel(eventLabel3);
        Assert.assertEquals("size of userLabels list should still be 3 (shouldn't add duplicates)", 3, userDSO.getUserLabels().size());
    }
}
