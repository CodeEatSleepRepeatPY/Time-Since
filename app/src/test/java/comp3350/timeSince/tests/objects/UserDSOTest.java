package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

@FixMethodOrder(MethodSorters.JVM)
public class UserDSOTest {
    private UserDSO userDSO;
    private String id;
    private String passwordHash;
    Calendar defaultDate;

    @Before
    public void setUp() {
        this.id = "bobby_g@gmail.com";
        this.passwordHash = "p4ssw0rd";
        defaultDate = Calendar.getInstance();

        this.userDSO = new UserDSO(1, id, defaultDate, passwordHash);
    }

    @Test
    public void testGetName() {
        String message = String.format("Initial name should be set to %s",
                this.id);

        Assert.assertEquals(message, this.id, this.userDSO.getName());
    }

    @Test
    public void testGetID() {
        String message = String.format("Initial uuid should be set to %s",
                this.id);

        Assert.assertEquals(message, this.id, this.userDSO.getEmail());
    }


    @Test
    public void testGetDateRegistered() {
        int wiggleRoom = 10;
        Calendar slightPast = Calendar.getInstance();
        slightPast.setTimeInMillis(System.currentTimeMillis() - wiggleRoom);
        Calendar slightFuture = Calendar.getInstance();
        slightFuture.setTimeInMillis(System.currentTimeMillis() + wiggleRoom);

        Calendar dateRegistered = this.userDSO.getDateRegistered();

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
                this.userDSO.getUserFavorites().size());
    }

    @Test
    public void testSetName() {
        String newName = "Gary";
        String message = String.format("The name should now be set to %s",
                newName);
        this.userDSO.setName(newName);

        Assert.assertEquals(message, newName, this.userDSO.getName());
    }

    @Test (expected = PasswordErrorException.class)
    public void testMeetsNewPasswordReq() {
        final int MIN_LENGTH = 8;
        String newPassword = "Hunter1";
        String message = String.format("Passwords should require a minimum " +
                "length of at least %d", MIN_LENGTH);

        assertFalse(message, UserDSO.meetsNewPasswordReq(newPassword));

        newPassword = "hunter12";
        message = "Passwords should require a capital.";
        assertFalse(message, UserDSO.meetsNewPasswordReq(newPassword));

        newPassword = "Hunter12";
        message = String.format("%s should pass the minimum requirements of " +
                        "having a capital letter, and being at least %d in length.",
                newPassword, MIN_LENGTH);
        assertTrue(message, UserDSO.meetsNewPasswordReq(newPassword));
    }

    @Test
    public void testSetNewPassword() {
        String newPasswordHash = "11111";
        String message = "setNewPassword should return true when passing in" +
                "the correct old password hash";

        assertTrue(message, this.userDSO.setNewPassword(this.passwordHash, newPasswordHash));

        message = String.format("The user's password hash should now " +
                "be set to %s", newPasswordHash);

        assertEquals(message, this.userDSO.getPasswordHash(), newPasswordHash);
    }

    @Test (expected = PasswordErrorException.class)
    public void testMatchesExistingPassword() {
        String otherPasswordHash = "Hunter12";
        String message = "matchesExistingPassword should return false when " +
                "given a different password hash.";

        assertFalse(message, this.userDSO.matchesExistingPassword(otherPasswordHash));

        message = "matchesExistingPassword should return true when given " +
                "the same password hash";

        assertTrue(message, this.userDSO.matchesExistingPassword(this.passwordHash));
    }

    @Test
    public void testEvents() {
        EventDSO event1 = new EventDSO(1, defaultDate, "Event1");
        EventDSO event2 = new EventDSO(2, defaultDate, "Event2");

        userDSO.addEvent(event1);
        userDSO.addEvent(event2);
        List<EventDSO> events = userDSO.getUserEvents();
        assertEquals("The user should have 2 events", 2, events.size());
        assertTrue("The user should contain event1", events.contains(event1));
        assertTrue("The user should contain event2", events.contains(event2));

        userDSO.removeEvent(event1);
        events = userDSO.getUserEvents();
        assertEquals("The user should now have 1 event", 1, events.size());
        assertFalse("The user should not have the removed event", events.contains(event1));
        assertTrue("The user should have event2", events.contains(event2));
    }

    @Test
    public void testLabels() {
        EventLabelDSO label1 = new EventLabelDSO(1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(1, "Label2");

        userDSO.addLabel(label1);
        userDSO.addLabel(label2);
        List<EventLabelDSO> labels = userDSO.getUserLabels();
        assertEquals("The user should have 2 labels", 2, labels.size());
        assertTrue("The user should contain label1", labels.contains(label1));
        assertTrue("The user should contain label2", labels.contains(label2));

        userDSO.removeLabel(label1);
        labels = userDSO.getUserLabels();
        assertEquals("The user should now have 1 label", 1, labels.size());
        assertFalse("The user should not have the removed label", labels.contains(label1));
        assertTrue("The user should have label2", labels.contains(label2));
    }

    @Test
    public void testFavorites() {
        // TODO
    }

    @Test
    public void testToString() {
        String expected = String.format("Name: %s, UserID: %s",
                userDSO.getName(), userDSO.getEmail());
        String message = "The User should display as: 'Name: ?name?, UserID: ?id?'";
        assertEquals(message, expected, userDSO.toString());

        userDSO.setName(null);
        expected = String.format("UserID: %s", userDSO.getEmail());
        message = "The User should display as: 'UserID: ?id?' when no name is given.";
        assertEquals(message, expected, userDSO.toString());

        UserDSO testUser = new UserDSO(-1, null, defaultDate, passwordHash);
        assertEquals("Nothing should be displayed if no name or id.",
                "", testUser.toString());
    }

    @Test
    public void testEquals() {
        UserDSO other = new UserDSO(1, "bobby_g@gmail.com", defaultDate, "12345");
        assertEquals("Users with the same ID and email should be equal",
                other, userDSO);
        other = new UserDSO(2, "bobby2_g@gmail.com", defaultDate, "12345");
        assertNotEquals("Users with different ID's should not be equal",
                other, userDSO);
    }

}
