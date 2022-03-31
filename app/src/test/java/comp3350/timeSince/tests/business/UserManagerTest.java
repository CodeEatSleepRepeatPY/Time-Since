package comp3350.timeSince.tests.business;

import org.junit.Before;
import org.junit.Test;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

public class UserManagerTest {
    private UserManager userManger;
    private IUserPersistence userDatabase;
    private Calendar defaultDate;


    @Before
    public void setUp(){
        userManger = new UserManager();
        userDatabase = Services.getUserPersistence();
        defaultDate = Calendar.getInstance();

        userDatabase.insertUser(new UserDSO("kevin@qq.com",
                defaultDate, "Kevin12345"));
        userDatabase.insertUser(new UserDSO("bob23@qq.com",
                defaultDate, "Bob1234"));
        userDatabase.insertUser(new UserDSO("James98@qq.com",
                defaultDate, "James1234"));
        userDatabase.insertUser(new UserDSO("Jack233@qq.com",
                defaultDate, "JACK1234"));
    }

    @Test
    public void accountCheckTest(){
        String testUserName1 = "kevin@qq.com";
        String testUserPassword1 = "Kevin12345";
        String testUserName2 = "James@qq.com";
        String testUserPassword2 = "kevin12345";

        assertTrue("kevin@qq.com is an existing username and Kevin12345 is a correct password should return true.",
                userManger.accountCheck(testUserName1,testUserPassword1));
        assertFalse("kevin is an existing username but kevin12345 is a incorrect password should return false.",
                userManger.accountCheck(testUserName1,testUserPassword2));
        assertFalse("James is not an existing username even if  Kevin12345 is a correct password should return false.",
                userManger.accountCheck(testUserName2,testUserPassword2));
    }

//    @Test
//    public void lengthCheckTest(){
//        String invalidPassword = "bob12";
//        String validPassword = "Bob12345";
//
//        assertFalse("Since the length of the bob12 is 5 should return false", userManger.lengthCheck(invalidPassword));
//        assertTrue("The length of Bob12345 is 8 should return true",userManger.lengthCheck(validPassword));
//    }

    @Test
    public void uniqueNameTest(){
        String user1 = "kevin12@qq.com";
        String user2 = "bob23@qq.com";

        assertTrue("kevin12 is not exist so it is unique, method returns true.",userManger.uniqueName(user1));
        assertFalse("bob23 is exist so it is not unique, returns false",userManger.uniqueName(user2));
    }

    @Test
    public void passwordRequirementsTest(){
        String password1 = "Bob12345";
        String confirmedPassword1 = "Bob12345";
        String confirmedPassword2 = "BoB12345";

        assertTrue("As Bob12345 has 1 capital letter, and user typed same password for two times should return true"
                ,userManger.passwordRequirements(password1,confirmedPassword1));
        assertFalse("Bob12345 is not same as BoB12345 should return false",userManger.passwordRequirements(password1,confirmedPassword2));
    }


    @Test
    public void loginProcessTest(){
        String userName1 = "bob23@qq.com";
        String password1 = "Bob1234";

        assertTrue("bob23@qq.com is in this list",userManger.loginProcess(userName1,password1));
    }
}
