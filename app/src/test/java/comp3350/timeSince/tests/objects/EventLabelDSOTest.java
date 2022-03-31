package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import comp3350.timeSince.objects.EventLabelDSO;

public class EventLabelDSOTest {
    private EventLabelDSO eventLabelDSO;
    private String name;

    @Before
    public void setUp() {
        name = "Super Secret Sauce";
        eventLabelDSO = new EventLabelDSO(1, name);
    }

    @Test
    public void testTestGetName() {
        String message = String.format("The initial name should be %s", name);
        assertEquals(message, name, eventLabelDSO.getName());
    }

    @Test
    public void testTestSetName() {
        String newName = "When You Have Time";
        String message = String.format("The name should now be set to %s", newName);

        eventLabelDSO.setName(newName);
        assertEquals(message, newName, this.eventLabelDSO.getName());
    }

    @Test
    public void testToString() {
        String expected = String.format("#%s", eventLabelDSO.getName());
        String message = "The Event Label should display as: '# ?labelName?'";
        assertEquals(message, expected, eventLabelDSO.toString());

        eventLabelDSO.setName(null);
        assertEquals("If label name does not exist, should display as: '#'",
                "#", eventLabelDSO.toString());
    }

    @Test
    public void testEquals() {
        EventLabelDSO other = new EventLabelDSO(1, "Garage");
        assertTrue("Event labels with the same ID should be equal",
                eventLabelDSO.equals(other));
        other = new EventLabelDSO(2, "Kitchen");
        assertFalse("Event labels with different ID's should not be equal",
                eventLabelDSO.equals(other));
    }
}
