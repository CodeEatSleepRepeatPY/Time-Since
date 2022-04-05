package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.business.EventManagerTest;
import comp3350.timeSince.tests.business.EventManagerIntegrationTest;
import comp3350.timeSince.tests.business.UserManagerTest;
import comp3350.timeSince.tests.objects.EventsWithLabelsTest;
import comp3350.timeSince.tests.objects.UsersWithEventsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UsersWithEventsTest.class,
        EventsWithLabelsTest.class,
        //EventManagerTest.class,
        UserManagerTest.class,
        EventManagerIntegrationTest.class
})
public class IntegrationTests {
}
