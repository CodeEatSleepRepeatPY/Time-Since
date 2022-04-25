package comp3350.timeSince;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import comp3350.timeSince.presentation.HomeActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageSystemTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityTestRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void homepage() {
        onView(withId(R.id.login)).check(matches(isDisplayed()));
        onView(withId(R.id.createAccount)).check(matches(isDisplayed()));

        onView(withId(R.id.login)).perform(click());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();

        onView(withId(R.id.createAccount)).perform(click());
        onView(withText("CREATE ACCOUNT")).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();
    }
}
