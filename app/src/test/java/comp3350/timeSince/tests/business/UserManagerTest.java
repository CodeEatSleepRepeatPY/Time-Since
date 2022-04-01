package comp3350.timeSince.tests.business;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserRegistrationFailedException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

public class UserManagerTest {
    private UserManager userManager;
    private File tempDB;

    @Before
    public void setUp() throws IOException {
        tempDB = TestUtils.copyDB();
        userManager = new UserManager(true);
    }

    @Test
    public void accountCheckTest() throws NoSuchAlgorithmException {
        String testUserName1 = "admin";
        String testUserPassword1 = "12345";
        String testUserPassword2 = "kevin12345";

        assertTrue("admin is an existing username and 12345 is a correct password should return true.",
                userManager.accountCheck(testUserName1, testUserPassword1));
        assertFalse("admin is an existing username but kevin12345 is a incorrect password should return false.",
                userManager.accountCheck(testUserName1, testUserPassword2));
        assertFalse("James is not an existing username even if  kevin12345 is not a correct password should return false.",
                userManager.accountCheck(testUserName1, testUserPassword2));
    }

    @Test
    public void uniqueNameTest() {
        String user1 = "kevin12@qq.com";
        String user2 = "admin";

        assertTrue("kevin12 is not exist so it is unique, method returns true.", userManager.uniqueName(user1));
        assertFalse("admin is exist so it is not unique, returns false", userManager.uniqueName(user2));
    }

    @Test (expected = PasswordErrorException.class)
    public void passwordRequirementsTest() {
        String password1 = "Bob12345";
        String password2 = "BoB123";

        assertTrue("As Bob12345 has 1 capital letter, and user typed same password for two times should return true"
                , userManager.passwordRequirements(password1));
        assertFalse("Bob123 is less than 8 should return false", userManager.passwordRequirements(password2));
    }

    @Test
    public void hashPasswordTest() throws NoSuchAlgorithmException {
        String inputPassword1 = "Kevin12345";
        String inputPassword2 = "kevin12345";
        //An expected hash password we get online by sha-256
        String expectedHashPassword = "5070da9022cc3a82869511c63c48d87d38e36eed9e03c94c619680a0cdeffea0";

        assertEquals("The result returned should equal to the expected hex String.", userManager.hashPassword(inputPassword1), expectedHashPassword);
        assertNotEquals("As K and k is not same, these two password are not equal.", userManager.hashPassword(inputPassword1), userManager.hashPassword(inputPassword2));
    }

    @Test (expected = DuplicateUserException.class)
    public void tryRegistrationTest() {
        String newUserName = "Emma@qq.com";
        String existUserName = "admin";
        String password = "Emmahappy99";
        String correctConfirmedPassword = "Emmahappy99";
        String wrongConfirmedPassword = "emmahappy99";

        try {
            assertTrue("Emma@qq.com is not exist in the db, and we typed the same valid password for twice.",
                    userManager.insertUser(newUserName, password, correctConfirmedPassword, null));
            assertFalse("The password and confirmed password are not same", userManager.insertUser(newUserName, password, wrongConfirmedPassword, null));
            assertFalse("admin is not exist in the db, should return false.",
                    userManager.insertUser(existUserName, password, correctConfirmedPassword, null));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUserByID() {

    }

    @Test
    public void updateUserName() {

    }

    @Test
    public void updateUserPassword() {

    }

    @Test
    public void addUserEvent() {

    }

    @Test
    public void addUserFavorite() {

    }

    @Test
    public void deleteUser() {

    }

    @Test
    public void getUserEvents() {

    }

    @Test
    public void getUserFavorites() {

    }

}
