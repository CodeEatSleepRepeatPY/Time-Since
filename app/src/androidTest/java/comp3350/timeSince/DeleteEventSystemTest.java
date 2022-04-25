package comp3350.timeSince;

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

import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.presentation.HomeActivity;
import comp3350.timeSince.utils.TestUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteEventSystemTest {

    private static UserEventManager userEventManager;

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setupDatabase() throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {
        Services.getUserPersistence(true);
        userEventManager = new UserEventManager("admin", true);
        TestUtils.setupDB();
        for(int i = 0; i < userEventManager.getUserEvents().size(); i++){
            if(!userEventManager.getUserEvents().get(i).getName().equals("event1")) {
                userEventManager.removeUserEvent(userEventManager.getUserEvents().get(i));
                i--;
            }
        }
        while(userEventManager.getUserEvents().size() > 1){
            userEventManager.removeUserEvent(userEventManager.getUserEvents().get(0));
        }
    }

    @Test
    public void deleteEventTest() {
        String username = "admin";
        String password = "12345";

        // navigate to login page
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));

        // login
        onView(withId(R.id.login)).perform(click());

        // click on the delete events button
        int beforeDeletion = userEventManager.getUserEvents().size();

        onView(withId(R.id.delete_event)).check(matches(isDisplayed())).perform(click());
        onView(withText("event1")).perform(click());
        onView(withText("YES")).perform(click());

        int afterDeletion = userEventManager.getUserEvents().size();

        Assert.assertEquals("An event should've been deleted", beforeDeletion - 1, afterDeletion);
    }

}
