package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.*;
import comp3350.timeSince.tests.persistence.*;

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
