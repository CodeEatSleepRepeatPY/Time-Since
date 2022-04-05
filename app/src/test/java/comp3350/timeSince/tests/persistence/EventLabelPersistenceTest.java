package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;

public class EventLabelPersistenceTest {

    private IEventLabelPersistence labelDatabase;
    private EventLabelDSO label1, label2, label3, label4;
    private UserDSO user;
    private String userID;
    private List<EventLabelDSO> labelList;

    @Before
    public void setUp() {
        labelDatabase = new EventLabelPersistence();
        userID = "admin";
        user = new UserDSO("admin", Calendar.getInstance(), "12345");
        label1 = new EventLabelDSO(userID, "Kitchen");
        label2 = new EventLabelDSO(userID, "Bathroom");
        label3 = new EventLabelDSO(userID, "Bedroom");
        label4 = new EventLabelDSO(userID, "Kitchen"); // for duplicate checks
        labelList = new ArrayList<>(Arrays.asList(label1, label2, label3));
    }

    @After
    public void tearDown() {
        List<EventLabelDSO> tempList = labelDatabase.getEventLabelList();

        if (tempList.contains(label1)) {
            labelDatabase.deleteEventLabel(user, label1);
        }
        if (tempList.contains(label2)) {
            labelDatabase.deleteEventLabel(user, label2);
        }
        if (tempList.contains(label3)) {
            labelDatabase.deleteEventLabel(user, label3);
        }
    }

    @Test
    public void testGetEventLabelList() {
        assertNotNull("Newly created database object should not be null",
                labelDatabase);
        assertEquals("Newly created database should have no users",
                0, labelDatabase.numLabels());

        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.insertEventLabel(user, label2);
        labelDatabase.insertEventLabel(user, label3);
        List<EventLabelDSO> actual = labelDatabase.getEventLabelList();

        assertTrue("Database should contain label1", actual.contains(label1));
        assertTrue("Database should contain label2", actual.contains(label2));
        assertTrue("Database should contain label3", actual.contains(label3));
        assertTrue("Database should have all existing event labels",
                actual.containsAll(labelList));
        assertTrue("Database should have all existing event labels",
                labelList.containsAll(actual));
        assertFalse("Database should not contain an event label that does not exist",
                actual.contains(new EventLabelDSO(userID, "Laundry")));
    }

    @Test
    public void testGetEventLabelByID() {
        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.insertEventLabel(user, label2);
        assertEquals("The correct event label should be returned if present",
                label1, labelDatabase.getEventLabelByID(userID, label1.getName()));
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testGetEventLabelByIDException() {
        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.getEventLabelByID(user.getID(), label3.getName());
    }

    @Test
    public void testInsertEventLabel() {
        assertEquals("Size of database should be 0", 0,
                labelDatabase.numLabels());

        labelDatabase.insertEventLabel(user, label1);
        assertEquals("Size of database should be 1", 1,
                labelDatabase.numLabels());

        assertEquals("Inserted event label should return", label2,
                labelDatabase.insertEventLabel(user, label2));
        assertEquals("Size of database should be 2", 2,
                labelDatabase.numLabels());

        labelDatabase.insertEventLabel(user, label3);
        assertEquals("Size of database should be 3", 3,
                labelDatabase.numLabels());

        assertEquals("Database should contain label2", label2,
                labelDatabase.getEventLabelByID(userID, label2.getName()));
    }

    @Test(expected = DuplicateEventLabelException.class)
    public void testInsertEventException() {
        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.insertEventLabel(user, label2);
        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.insertEventLabel(user, label4);
    }

    @Test
    public void testDeleteEventLabel() {
        labelDatabase.insertEventLabel(user, label1);
        labelDatabase.insertEventLabel(user, label2);
        labelDatabase.insertEventLabel(user, label3);

        assertEquals("Size of database should be 3", 3,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(user, label2);
        assertEquals("Size of database should be 2", 2,
                labelDatabase.numLabels());
        assertEquals("If event label exists, return the label that was deleted", label1,
                labelDatabase.deleteEventLabel(user, label1));
        assertEquals("Size of database should be 1", 1,
                labelDatabase.numLabels());
        labelDatabase.deleteEventLabel(user, label3);
        assertEquals("Size of database should be 0", 0,
                labelDatabase.numLabels());
    }

    @Test(expected = EventLabelNotFoundException.class)
    public void testDeleteEventLabelException() {
        labelDatabase.deleteEventLabel(user, label4); // should not be able to delete
                                                        // event label not in db
    }

}
