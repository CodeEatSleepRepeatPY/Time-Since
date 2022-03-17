package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.*;
import comp3350.timeSince.tests.persistence.*;
import comp3350.timeSince.tests.business.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventPersistenceTest.class,
        UserPersistenceTest.class,
        EventDSOTest.class,
        EventLabelDSOTest.class,
        UserDSOTest.class,
        UserManagerTest.class
})

public class AllTests {

}
