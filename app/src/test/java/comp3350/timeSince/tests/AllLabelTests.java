package comp3350.timeSince.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.timeSince.tests.objects.EventLabelDSOTest;
import comp3350.timeSince.tests.persistence.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventLabelDSOTest.class,
        EventLabelPersistenceTest.class,
        LabelPersistenceHSQLDBTest.class
})

public class AllLabelTests {
}
