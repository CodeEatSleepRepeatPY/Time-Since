package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.business.EventDisplayTest;
import comp3350.timeSince.tests.business.EventManagerTest;
import comp3350.timeSince.tests.business.UserManagerTest;
import comp3350.timeSince.tests.objects.EventsWithLabelsTest;
import comp3350.timeSince.tests.objects.UsersWithEventsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventsWithLabelsTest.class,
        UsersWithEventsTest.class,
        UserManagerTest.class,
        EventManagerTest.class,
        EventDisplayTest.class
})
public class IntegrationTests {
}
