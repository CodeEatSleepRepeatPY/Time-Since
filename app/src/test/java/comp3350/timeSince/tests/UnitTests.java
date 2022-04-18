package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.*;
import comp3350.timeSince.tests.persistence.fakes.EventLabelPersistenceTest;
import comp3350.timeSince.tests.persistence.fakes.EventPersistenceTest;
import comp3350.timeSince.tests.persistence.fakes.UserPersistenceTest;
import comp3350.timeSince.tests.persistence.hsqldb.EventPersistenceHSQLDBTest;
import comp3350.timeSince.tests.persistence.hsqldb.LabelPersistenceHSQLDBTest;
import comp3350.timeSince.tests.persistence.hsqldb.UserPersistenceHSQLDBTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventPersistenceTest.class,
        EventLabelPersistenceTest.class,
        UserPersistenceTest.class,
        EventDSOTest.class,
        EventLabelDSOTest.class,
        UserDSOTest.class,
        UserPersistenceHSQLDBTest.class,
        LabelPersistenceHSQLDBTest.class,
        EventPersistenceHSQLDBTest.class
})

public class UnitTests {
}
