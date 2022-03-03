package comp3350.timeSince.tests.objects;

import comp3350.timeSince.objects.UserDSO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;

public class UserDSOTest {
    private UserDSO userDSO;
    private String name;
    private UserDSO.MembershipType membershipType;
    private String uuid;
    private String passwordHash;

    @Before
    public void setUp() {
        this.name = "Bob";
        this.membershipType = UserDSO.MembershipType.free;
        this.uuid = "bobby_g@gmail.com";
        this.passwordHash = "p4ssw0rd";

        this.userDSO = new UserDSO(name, membershipType, uuid, passwordHash);
    }

    @Test
    public void testGetName() {
        String message = String.format("Initial name should be set to %s",
                this.name);

        Assert.assertEquals(message, this.name, this.userDSO.getName());
    }

    @Test
    public void testGetMembershipType() {
        String message = String.format("Initial membership type should be set" +
                "to %s", this.membershipType.name());

        Assert.assertEquals(message, this.membershipType,
                this.userDSO.getMembershipType());
    }

    @Test
    public void testGetUuid() {
        String message = String.format("Initial uuid should be set to %s",
                this.uuid);

        Assert.assertEquals(message, this.uuid, this.userDSO.getUuid());
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
        this.userDSO.setUuid(newUuid);

        Assert.assertEquals(message, newUuid, this.userDSO.getUuid());
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
}
