package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.EventDSOTest;
import comp3350.timeSince.tests.objects.EventLabelDSOTest;
import comp3350.timeSince.tests.objects.UserDSOTest;
import comp3350.timeSince.tests.persistence.FakeDBUnitTests;
import comp3550.timeSince.tests.business.UserManagerTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FakeDBUnitTests.class,
        EventDSOTest.class,
        EventLabelDSOTest.class,
        UserDSOTest.class,
        UserManager.class
})
public class AllTests
{

}
