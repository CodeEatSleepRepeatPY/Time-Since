package comp3350.timeSince.tests.persistence.hsqldb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;
import comp3350.timeSince.tests.persistence.utils.TestUtils;

@FixMethodOrder(MethodSorters.JVM)
public class LabelPersistenceHSQLDBTest {

    private IEventLabelPersistence labelDatabase;
    private EventLabelDSO label1, label2, label3, label4;
    private List<EventLabelDSO> labelList;
    private static final int initialCount = InitialDatabaseState.NUM_LABELS;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyDB();
        labelDatabase = Services.getEventLabelPersistence(true);

        label1 = new EventLabelDSO(initialCount + 1, "Kitchen");
        label2 = new EventLabelDSO(initialCount + 2, "Bathroom");
        label3 = new EventLabelDSO(initialCount + 3, "Bedroom");
        label4 = new EventLabelDSO(initialCount + 2, "Garage"); // for duplicate checks
        labelList = new ArrayList<>(Arrays.asList(label1, label2, label3));
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetEventLabelList() {
        assertNotNull("Newly created database object should not be null",
                labelDatabase);
        assertEquals("Newly created database should have " + initialCount + " labels",
                initialCount, labelDatabase.numLabels());

        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label2);
        labelDatabase.insertEventLabel(label3);
        List<EventLabelDSO> actual = labelDatabase.getEventLabelList();

        assertTrue("Database should contain label1", actual.contains(label1));
        assertTrue("Database should contain label2", actual.contains(label2));
        assertTrue("Database should contain label3", actual.contains(label3));
        assertTrue("Database should have all existing event labels",
                actual.containsAll(labelList));
        assertFalse("Database should not contain an event label that does not exist",
                actual.contains(new EventLabelDSO(initialCount + 5, "Laundry")));
    }

    @Test
    public void testGetEventLabelByID() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label2);
        assertEquals("The correct event label should be returned if present",
                label1, labelDatabase.getEventLabelByID(label1.getID()));
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testGetEventLabelByIDException() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.getEventLabelByID(label3.getID());
    }

    @Test
    public void testInsertEventLabel() {
        assertEquals("Size of database should be " + initialCount, initialCount,
                labelDatabase.numLabels());

        labelDatabase.insertEventLabel(label1);
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                labelDatabase.numLabels());

        assertEquals("Inserted event label should return",
                label2, labelDatabase.insertEventLabel(label2));
        assertEquals("Size of database should be 2", initialCount + 2,
                labelDatabase.numLabels());

        labelDatabase.insertEventLabel(label3);
        assertEquals("Size of database should be 3", initialCount + 3,
                labelDatabase.numLabels());

        assertEquals("Database should contain label2",
                label2, labelDatabase.getEventLabelByID(label2.getID()));
    }

    @Test(expected = DuplicateEventLabelException.class)
    public void testInsertEventException() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label1);
    }

    @Test
    public void testUpdateEventLabel() {
        labelDatabase.insertEventLabel(label1);
        assertEquals("Size of database should be 1", initialCount + 1,
                labelDatabase.numLabels());
        labelDatabase.updateEventLabelName(label1, "hello");
        assertEquals("New attributes should match", "hello",
                labelDatabase.getEventLabelByID(label1.getID()).getName());

        assertEquals("Updated label should be returned", "good-bye",
                labelDatabase.updateEventLabelName(label1, "good-bye").getName());
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testUpdateEventLabelException() {
        // should not be able to update an event label not in db
        labelDatabase.updateEventLabelName(label1, "badTest");
    }

    @Test
    public void testDeleteEventLabel() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label2);
        labelDatabase.insertEventLabel(label3);

        assertEquals("Size of database should be 3", initialCount + 3,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(label2);
        assertEquals("Size of database should be 2", initialCount + 2,
                labelDatabase.numLabels());
        assertEquals("If event label exists, return the label that was deleted", label1,
                labelDatabase.deleteEventLabel(label1));
        assertEquals("Size of database should be 1", initialCount + 1,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(label3);
        assertEquals("Size of database should be 0", initialCount,
                labelDatabase.numLabels());
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testDeleteEventLabelException() {
        labelDatabase.deleteEventLabel(label4); // should not be able to delete
        // event label not in db
    }

    @Test
    public void testGetNextID() {
        assertEquals("The first ID should be 1",
                initialCount + 1, labelDatabase.getNextID());
        labelDatabase.insertEventLabel(label1);
        assertEquals("The ID of the first label inserted should be 1",
                initialCount + 1, label1.getID());

        assertEquals("The second ID should be 2",
                initialCount + 2, labelDatabase.getNextID());
        labelDatabase.insertEventLabel(label2);
        assertEquals("The ID of the second label inserted should be 2",
                initialCount + 2, label2.getID());

        labelDatabase.insertEventLabel(label3);
        try {
            labelDatabase.insertEventLabel(label3);
        } catch (DuplicateEventLabelException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("The next ID after three labels, with one duplicate attempt should be 4.",
                initialCount + 4, labelDatabase.getNextID());

        labelDatabase.deleteEventLabel(label2);
        assertNotEquals("The next ID after a deletion should not be the deleted ID.",
                label2.getID(), labelDatabase.getNextID());
        assertEquals("The next ID should be 4", initialCount + 4, labelDatabase.getNextID());
    }

}
