package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.*;
import comp3350.timeSince.tests.persistence.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventDSOTest.class,
        EventsWithLabelsTest.class,
        EventPersistenceTest.class,
        EventPersistenceHSQLDBTest.class
})

public class AllEventTests {
}
