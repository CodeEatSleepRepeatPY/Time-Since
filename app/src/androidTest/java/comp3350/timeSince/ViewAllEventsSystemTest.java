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
public class ViewAllEventsSystemTest {

    private static UserEventManager userEventManager;

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setupDatabase() throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {
        Services.getUserPersistence(true);
        userEventManager = new UserEventManager("admin", true);
        TestUtils.setupDB();
    }

    @Test
    public void viewAllEvents() {
        String username = "admin";
        String password = "12345";

        // navigate to login page
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));

        // login
        onView(withId(R.id.login)).perform(click());

        // check that the events are showing up
        onView(withText("event1")).check(matches(isDisplayed()));
        onView(withText("event2")).check(matches(isDisplayed()));
        onView(withText("event3")).check(matches(isDisplayed()));

        Assert.assertTrue("There should be at least 3 events in the user's events list",
                userEventManager.getUserEvents().size() >= 3);
    }
}
