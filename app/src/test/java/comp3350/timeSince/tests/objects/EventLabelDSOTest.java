package comp3350.timeSince.tests.objects;

import comp3350.timeSince.objects.EventLabelDSO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventLabelDSOTest {
    private EventLabelDSO eventLabelDSO;
    private String name;
    private EventLabelDSO.Color color;

    @Before
    public void setUp() {
        this.name = "Super Secret Sauce";
        this.color = EventLabelDSO.Color.red;

        this.eventLabelDSO = new EventLabelDSO(name);
        eventLabelDSO.setColor(color);
    }

    @Test
    public void testTestGetName() {
        String message = String.format("The initial name should be %s",
                this.name);

        Assert.assertEquals(message, this.name, this.eventLabelDSO.getName());
    }

    @Test
    public void testGetColor() {
        String message = String.format("The initial color should be %s",
                this.color.name());

        Assert.assertEquals(message, this.color, this.eventLabelDSO.getColor());
    }

    @Test
    public void testTestSetName() {
        String newName = "When You Have Time";
        String message = String.format("The name should now be set to %s",
                newName);
        this.eventLabelDSO.setName(newName);

        Assert.assertEquals(message, newName, this.eventLabelDSO.getName());
    }

    @Test
    public void testSetColor() {
        EventLabelDSO.Color newColor = EventLabelDSO.Color.yellow;
        String message = String.format("The color should now be set to %s",
                newColor.name());
        this.eventLabelDSO.setColor(newColor);

        Assert.assertEquals(message, newColor, this.eventLabelDSO.getColor());
    }
}
