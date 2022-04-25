package comp3350.timeSince;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
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
public class ViewSingleEventSystemTest {

    private static UserEventManager userEventManager;

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setupDatabase() throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {
        Services.getUserPersistence(true);
        TestUtils.setupDB();
        userEventManager = new UserEventManager("admin", true);
    }

    @Test
    public void viewSingleEventTest() {
        String username = "admin";
        String password = "12345";
        boolean isFavorite = false;

        // navigate to login page
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.login)).perform(click());

        // navigate to the view single event screen
        onView(withText("event1")).perform(click());

        // check that we are on the correct page
        onView(withText("event1")).check(matches(isDisplayed()));
        onView(withText("FAVORITE")).check(matches(isDisplayed()));
        onView(withText("TAGS")).check(matches(isDisplayed()));
        onView(withText("DONE!")).check(matches(isDisplayed()));

        closeSoftKeyboard();

        // add event1 to favourites
        onView(withId(R.id.event_favorite_button)).perform(click());

        // go back
        pressBack();

        // go to event1 view single event
        onView(withText("event1")).perform(click());

        for(int i = 0; i < userEventManager.getUserFavorites().size(); i++){
            if(userEventManager.getUserFavorites().get(i).getName().equals("event1")){
                isFavorite = true;
            }
        }
        Assert.assertTrue("event1 should be favorited", isFavorite);

        // remove event1 from favourites
        onView(withId(R.id.event_favorite_button)).perform(click());

        closeSoftKeyboard();

        // go back
        pressBack();

        // go to event1 view single event
        onView(withText("event1")).perform(click());

        isFavorite = false;
        for(int i = 0; i < userEventManager.getUserFavorites().size(); i++){
            if(userEventManager.getUserFavorites().get(i).getName().equals("event1")){
                isFavorite = true;
            }
        }
        Assert.assertFalse("event1 should not be favorited", isFavorite);

        // write description and change title of event1
        onView(withId(R.id.event_name)).perform(clearText());
        onView(withId(R.id.event_name)).perform(typeText("event1changed"));
        onView(withId(R.id.event_description)).perform(typeText("This is event1 description"));
        onView(withId(R.id.event_done_button)).perform(click());

        // close keyboard
        closeSoftKeyboard();

        // go back
        pressBack();

        onView(withText("event1changed")).check(matches(isDisplayed()));

        // check that event1 still has the description after re-viewing the event
        onView(withText("event1changed")).perform(click());
        onView(withText("This is event1 description")).check(matches(isDisplayed()));

        // change title back to what it was before
        onView(withId(R.id.event_name)).perform(clearText());
        onView(withId(R.id.event_name)).perform(typeText("event1")).check(matches(isDisplayed()));

        //close keyboard
        closeSoftKeyboard();

        // go back to save everything
        pressBack();
    }

}
