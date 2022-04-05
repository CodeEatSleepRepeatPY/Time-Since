package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public class EventLabelDSOTest {
    private EventLabelDSO eventLabelDSO;
    private UserDSO user;
    private String name;

    @Before
    public void setUp() {
        name = "Super Secret Sauce";
        user = new UserDSO("admin", Calendar.getInstance(), "12345");
        eventLabelDSO = new EventLabelDSO(user.getID(), name);
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
    public void testValidate() {
        assertTrue("An Event Label with both a valid ID and name should be valid.",
                eventLabelDSO.validate());

        EventLabelDSO badLabel = new EventLabelDSO(null, null);
        assertFalse("An Event Label with both invalid parameters should not be valid.",
                badLabel.validate());

        badLabel = new EventLabelDSO(null, "hello");
        assertFalse("An Event Label with an invalid ID should not be valid.",
                badLabel.validate());

        badLabel = new EventLabelDSO(user.getID(),null);
        assertFalse("An Event Label with an invalid name should not be valid.",
                badLabel.validate());
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
        EventLabelDSO other = new EventLabelDSO(user.getID(), name);
        assertTrue("Event labels with the same ID should be equal",
                eventLabelDSO.equals(other));
        other = new EventLabelDSO(user.getID(), "Kitchen");
        assertFalse("Event labels with different ID's should not be equal",
                eventLabelDSO.equals(other));
    }
}
