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

import comp3350.timeSince.business.exceptions.PasswordErrorException;
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
    public void testSetName() {
        String newName = "Gary";
        String message = String.format("The name should now be set to %s",
                newName);
        this.userDSO.setName(newName);

        Assert.assertEquals(message, newName, this.userDSO.getName());
    }

    @Test
    public void testSetNewEmail() {
        String newEmail = "bobby2@gmail.com";
        String message = String.format("The email should now be set to %s", newEmail);
        userDSO.setNewEmail(id, newEmail);
        assertEquals(message, newEmail, userDSO.getEmail());
    }

    @Test
    public void testMeetsNewPasswordReq() {
        final int MIN_LENGTH = 8;
        String newPassword = "Hunter12";

        String message = String.format("%s should pass the minimum requirements of " +
                        "having a capital letter, and being at least %d in length.",
                newPassword, MIN_LENGTH);
        assertTrue(message, UserDSO.meetsNewPasswordReq(newPassword));
    }

    @Test (expected = PasswordErrorException.class)
    public void testMeetsNewPasswordReqExceptionA() {
        final int MIN_LENGTH = 8;
        String newPassword = "Hunter1";
        String message = String.format("Passwords should require a minimum " +
                "length of at least %d", MIN_LENGTH);
        assertFalse(message, UserDSO.meetsNewPasswordReq(newPassword));
    }

    @Test (expected = PasswordErrorException.class)
    public void testMeetsNewPasswordReqExceptionB() {
        String newPassword = "hunter12";
        String message = "Passwords should require a capital.";
        assertFalse(message, UserDSO.meetsNewPasswordReq(newPassword));

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

    @Test
    public void testMatchesExistingPassword() {
        String message = "matchesExistingPassword should return true when given " +
                "the same password hash";

        assertTrue(message, this.userDSO.matchesExistingPassword(this.passwordHash));
    }

    @Test (expected = PasswordErrorException.class)
    public void testMatchesExistingPasswordException() {
        String otherPasswordHash = "Hunter12";
        userDSO.matchesExistingPassword(otherPasswordHash);
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
