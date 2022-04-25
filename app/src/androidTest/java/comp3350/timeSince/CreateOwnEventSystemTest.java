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

import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.presentation.HomeActivity;
import comp3350.timeSince.utils.TestUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateOwnEventSystemTest {

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
    public void createOwnEventTest() {
        String username = "admin";
        String password = "12345";
        // verifyText is used to make sure that the user was logged in
        // and redirected to the next Intent/Activity
        boolean eventFound = false;
        boolean isFavorite = false;

        // navigate to login page
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.login)).perform(click());

        // navigate to the "create your own event" screen
        onView(withId(R.id.add_new_event)).perform(click());

        // create the event and add it to the list
        onView(withId(R.id.event_name)).perform(typeText("event4"));
        onView(withId(R.id.event_description)).perform(typeText("event4 description"));
        onView(withId(R.id.favorite_btn)).perform(click());
        onView(withId(R.id.save_event)).perform(click());

        // check that the event is displaying and it was indeed added to the list
        onView(withText("event4")).check(matches(isDisplayed()));

        for(int i = 0; i < userEventManager.getUserEvents().size(); i++){
            if(userEventManager.getUserEvents().get(i).getName().equals("event4")){
                eventFound = true;
            }
        }
        Assert.assertTrue("event4 should be in the user's events list", eventFound);

        for(int i = 0; i < userEventManager.getUserFavorites().size(); i++){
            if(userEventManager.getUserFavorites().get(i).getName().equals("event4")){
                isFavorite = true;
            }
        }
        Assert.assertTrue("event4 should be in the user favorites list", isFavorite);

        //close keyboard
        closeSoftKeyboard();
    }

}
