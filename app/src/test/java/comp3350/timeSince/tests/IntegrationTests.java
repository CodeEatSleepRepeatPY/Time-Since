package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.business.EventManagerTest;
import comp3350.timeSince.tests.business.UserManagerTest;
import comp3350.timeSince.tests.objects.EventsWithLabelsTest;
import comp3350.timeSince.tests.objects.UsersWithEventsTest;
import comp3350.timeSince.tests.persistence.UserConnectionsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UsersWithEventsTest.class,
        EventsWithLabelsTest.class,
        UserManagerTest.class,
        EventManagerTest.class,
        UserConnectionsTest.class
})
public class IntegrationTests {
}
