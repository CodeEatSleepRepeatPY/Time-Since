package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.*;
import comp3350.timeSince.tests.persistence.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserDSOTest.class,
        UsersWithEventsTest.class,
        UserPersistenceTest.class,
        UserPersistenceHSQLDBTest.class,
        UserConnectionsTest.class
})

public class AllUserTests {
}
