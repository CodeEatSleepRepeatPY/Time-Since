package comp3350.timeSince;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.presentation.HomeActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterSystemTest { // this class needs to be fixed still

    private static UserManager userManager;
    private static IUserPersistence userPersistence;
    private String email = "user@gmail.com";
    private String password = "Password123!@#";

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setup() {
        userPersistence = Services.getUserPersistence(true);
        userManager = new UserManager(true);
        try{
            userManager.deleteUser(email);
        } catch(UserNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void register() {
        // verifyText is used to make sure that the user was registered
        // and redirected to the next Intent/Activity
        String verifyText = "Create your Event";

        // navigate to the registration page
        onView(withId(R.id.createAccount)).perform(click());
        onView(withId(R.id.username)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.confirm_password)).perform(typeText(password));
        onView(withId(R.id.login)).perform(click());

        // check that user was created
        Assert.assertNotNull("user@gmail.com should exist in the database", userPersistence.getUserByEmail("user@gmail.com"));

        // check that we are on the correct page after registering
        onView(withText(verifyText)).check(matches(isDisplayed()));
        onView(withText("SAVE")).check(matches(isDisplayed()));

        // close the keyboard
        closeSoftKeyboard();
    }

}
