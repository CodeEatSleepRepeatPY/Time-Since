package comp3350.timeSince;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
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

import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.presentation.HomeActivity;
import comp3350.timeSince.utils.TestUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MarkEventDoneSystemTest {

    private UserEventManager userEventManager;

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setupDatabase() throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {
        Services.getUserPersistence(true);
        userEventManager = new UserEventManager("admin", true);
        TestUtils.setupDB();
    }

    @Test
    public void markEventAsDone(){
        String username = "admin";
        String password = "12345";
        // verifyText is used to make sure that the user was logged in
        // and redirected to the next Intent/Activity
        String verifyText = "All Events";

        // navigate to login page
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.login)).perform(click());

        //check that we are on the correct page
        onView(withText(verifyText)).check(matches(isDisplayed()));

        //check that we can see our events
        onView(withText("event1")).check(matches(isDisplayed()));
        onView(withText("event2")).check(matches(isDisplayed()));
        onView(withText("event3")).check(matches(isDisplayed()));

        //click on event1
        onView(withText("event1")).perform(click());
        onView(withId(R.id.event_done_button)).perform(click());

        //go back
        pressBack();

        for(int i = 0; i < userEventManager.getUserEvents().size(); i++){
            if(userEventManager.getUserEvents().get(i).getName().equals("event1")){
                Assert.assertNull("event1 target finish time should be null",
                        userEventManager.getUserEvents().get(i).getTargetFinishTime());
                break;
            }
        }

        //close keyboard
        closeSoftKeyboard();
    }

}
