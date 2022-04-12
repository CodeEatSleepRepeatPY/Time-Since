package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.interfaces.IUserManager;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class UserManagerTest {
    private IUserManager IUserManager;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();
        IUserManager = new UserManager(true);
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void accountCheckTest() throws NoSuchAlgorithmException {
        String testUserName1 = "admin";
        String testUserPassword1 = "12345";
        String testUserPassword2 = "kevin12345";

        assertTrue("admin is an existing username and 12345 is a correct password should return true.",
                IUserManager.accountCheck(testUserName1, testUserPassword1));
        assertFalse("admin is an existing username but kevin12345 is a incorrect password should return false.",
                IUserManager.accountCheck(testUserName1, testUserPassword2));
        assertFalse("James is not an existing username even if  kevin12345 is not a correct password should return false.",
                IUserManager.accountCheck(testUserName1, testUserPassword2));
    }

    @Test
    public void uniqueNameTest() {
        String user1 = "kevin12@qq.com";
        String user2 = "admin";

        assertTrue("kevin12 is not exist so it is unique, method returns true.", IUserManager.uniqueName(user1));
        assertFalse("admin is exist so it is not unique, returns false", IUserManager.uniqueName(user2));
    }

    @Test(expected = PasswordErrorException.class)
    public void passwordRequirementsTest() {
        String password1 = "Bob12345";
        String password2 = "BoB123";

        assertTrue("As Bob12345 has 1 capital letter, and user typed same password for two times should return true"
                , IUserManager.passwordRequirements(password1));
        assertFalse("Bob123 is less than 8 should return false", IUserManager.passwordRequirements(password2));
    }

    @Test
    public void hashPasswordTest() throws NoSuchAlgorithmException {
        String inputPassword1 = "Kevin12345";
        String inputPassword2 = "kevin12345";
        //An expected hash password we get online by sha-256
        String expectedHashPassword = "5070da9022cc3a82869511c63c48d87d38e36eed9e03c94c619680a0cdeffea0";

        assertEquals("The result returned should equal to the expected hex String.", IUserManager.hashPassword(inputPassword1), expectedHashPassword);
        assertNotEquals("As K and k is not same, these two password are not equal.", IUserManager.hashPassword(inputPassword1), IUserManager.hashPassword(inputPassword2));
    }

    @Test
    public void tryRegistrationTest() {
        String newUserName = "Emma@qq.com";
        String existUserName = "admin";
        String password = "Emmahappy99";
        String correctConfirmedPassword = "Emmahappy99";
        String wrongConfirmedPassword = "emmahappy99";

        try {
            assertNotNull("Emma@qq.com is not exist in the db, and we typed the same valid " +
                    "password for twice.", IUserManager.insertUser(newUserName, password,
                    correctConfirmedPassword, null));
            assertNull("The password and confirmed password are not same",
                    IUserManager.insertUser(newUserName, password, wrongConfirmedPassword, null));
        } catch (DuplicateUserException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUserByID() {
        assertNotNull("the user admin should exist in the database",
                IUserManager.getUserByEmail("admin"));

        assertNotNull("the user kristjaf@myumanitoba.ca should exist in the database",
                IUserManager.getUserByEmail("kristjaf@myumanitoba.ca"));

        assertEquals("admin should have the event named New Toothbrush",
                "New Toothbrush",
                IUserManager.getUserByEmail("admin").getUserEvents().get(0).getName());

        assertEquals("kristjaf@myumanitoba.ca should have the event named New Toothbrush",
                "New Toothbrush",
                IUserManager.getUserByEmail("kristjaf@myumanitoba.ca").getUserEvents().get(0).getName());
    }

    @Test
    public void updateUserName() {
        assertNotNull("admin's username should've been updated", IUserManager.updateUserName("admin", "wow"));
        assertEquals("admin should now have the username 'wow'", "wow", IUserManager.getUserByEmail("admin").getName());
        assertNotNull("wow's username should've been updated back to admin", IUserManager.updateUserName("admin", "admin"));
        assertEquals("wow should now have the username 'admin'", "admin", IUserManager.getUserByEmail("admin").getName());
    }

    @Test
    public void updateUserPassword() throws NoSuchAlgorithmException {
        UserDSO result = IUserManager.updateUserPassword("admin",  "A12345678");
        assertNotNull("admin's password should've been updated", result);

        assertEquals("admin's password should now be the sha256 hash of 'A12345678'",
                "3b4e266a89805c9d020f9aca6638ad63e8701fc8c75c0ca1952d14054d1f10cf",
                IUserManager.getUserByEmail("admin").getPasswordHash());
    }

}
