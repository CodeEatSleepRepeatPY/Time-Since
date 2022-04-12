package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;

@FixMethodOrder(MethodSorters.JVM)
public class EventLabelPersistenceTest {

    private IEventLabelPersistence labelDatabase;
    private EventLabelDSO label1, label2, label3, label4;
    private List<EventLabelDSO> labelList;
    private final int initialCount = 9;

    @Before
    public void setUp() {
        labelDatabase = new EventLabelPersistence();
        label1 = new EventLabelDSO(initialCount + 1, "Label1");
        label2 = new EventLabelDSO(initialCount + 2, "Label2");
        label3 = new EventLabelDSO(initialCount + 3, "Label3");
        label4 = new EventLabelDSO(initialCount + 2, "Label4"); // for duplicate checks
        labelList = new ArrayList<>(Arrays.asList(label1, label2, label3));
    }

    @After
    public void tearDown() {
        List<EventLabelDSO> tempList = labelDatabase.getEventLabelList();

        if (tempList.contains(label1)) {
            labelDatabase.deleteEventLabel(label1);
        }
        if (tempList.contains(label2)) {
            labelDatabase.deleteEventLabel(label2);
        }
        if (tempList.contains(label3)) {
            labelDatabase.deleteEventLabel(label3);
        }
    }

    @Test
    public void testGetEventLabelList() {
        assertNotNull("Newly created database object should not be null",
                labelDatabase);
        assertEquals("Newly created database should have " + initialCount + " users",
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
        assertEquals("Size of database should be " + (initialCount + 1), initialCount +1,
                labelDatabase.numLabels());

        assertEquals("Inserted event label should return", label2,
                labelDatabase.insertEventLabel(label2));
        assertEquals("Size of database should be " + (initialCount + 2), initialCount + 2,
                labelDatabase.numLabels());

        labelDatabase.insertEventLabel(label3);
        assertEquals("Size of database should be " + (initialCount + 3), initialCount + 3,
                labelDatabase.numLabels());

        assertEquals("Database should contain label2", label2,
                labelDatabase.getEventLabelByID(label2.getID()));
    }

    @Test(expected = DuplicateEventLabelException.class)
    public void testInsertEventException() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label2);
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label4);
    }

    @Test
    public void testUpdateEventLabel() {
        labelDatabase.insertEventLabel(label1);
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                labelDatabase.numLabels());
        label1.setName("hello");
        labelDatabase.updateEventLabel(label1);
        assertEquals("New attributes should match", "hello",
                labelDatabase.getEventLabelByID(label1.getID()).getName());

        label1.setName("good-bye");
        assertEquals("Updated label should be returned", "good-bye",
                labelDatabase.updateEventLabel(label1).getName());
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testUpdateEventLabelException() {
        // should not be able to update an event label not in db
        labelDatabase.updateEventLabel(label1);
    }

    @Test
    public void testDeleteEventLabel() {
        labelDatabase.insertEventLabel(label1);
        labelDatabase.insertEventLabel(label2);
        labelDatabase.insertEventLabel(label3);

        assertEquals("Size of database should be " + (initialCount + 3), initialCount + 3,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(label2);
        assertEquals("Size of database should be " + (initialCount + 2), initialCount + 2,
                labelDatabase.numLabels());
        assertEquals("If event label exists, return the label that was deleted", label1,
                labelDatabase.deleteEventLabel(label1));
        assertEquals("Size of database should be " + (initialCount + 1), initialCount + 1,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(label3);
        assertEquals("Size of database should be " + initialCount, initialCount,
                labelDatabase.numLabels());
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testDeleteEventLabelException() {
        labelDatabase.deleteEventLabel(label4); // should not be able to delete
                                                        // event label not in db
    }

    @Test
    public void testGetNextID() {
        assertEquals("The first ID should be " + (initialCount + 1),
                initialCount + 1, labelDatabase.getNextID());
        labelDatabase.insertEventLabel(label1);
        assertEquals("The ID of the first label inserted should be " + (initialCount + 1),
                initialCount + 1, label1.getID());

        assertEquals("The second ID should be " + (initialCount + 2),
                initialCount + 2, labelDatabase.getNextID());
        labelDatabase.insertEventLabel(label2);
        assertEquals("The ID of the second label inserted should be " + (initialCount + 2),
                initialCount + 2, label2.getID());

        labelDatabase.insertEventLabel(label3);
        try {
            labelDatabase.insertEventLabel(label3);
        } catch (DuplicateEventLabelException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("The next ID after three labels, with one duplicate attempt should be " + (initialCount + 4),
                initialCount + 4, labelDatabase.getNextID());

        labelDatabase.deleteEventLabel(label2);
        assertNotEquals("The next ID after a deletion should not be the deleted ID.",
                label2.getID(), labelDatabase.getNextID());
        assertEquals("The next ID should be " + (initialCount + 4), initialCount + 4, labelDatabase.getNextID());
    }

}
