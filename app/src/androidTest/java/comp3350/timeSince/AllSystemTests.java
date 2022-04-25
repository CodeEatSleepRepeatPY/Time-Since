package comp3350.timeSince;

import androidx.test.filters.LargeTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@LargeTest
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateOwnEventSystemTest.class,
        DeleteEventSystemTest.class,
        EventLabelSystemTest.class,
        HomePageSystemTest.class,
        LoginSystemTest.class,
        MarkEventDoneSystemTest.class,
        RegisterSystemTest.class,
        ViewAllEventsSystemTest.class,
        ViewSingleEventSystemTest.class
})

public class AllSystemTests {
}
