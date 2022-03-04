package comp3350.timeSince.tests.objects;
import org.junit.Before;
import org.junit.Test;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import static org.junit.Assert.*;
import java.util.Date;

public class EventDSOTest {

    public static EventDSO event;
    public static String name;
    public static Date date;
    public static Date targetDate;

    @Before
    public void instantiateObject(){
        String message = String.format("The event should not be null");
        name = "event0";
        date = new Date( System.currentTimeMillis() );
        event = new EventDSO(name, date);
        assertNotNull(message, event);
        targetDate = new Date(System.currentTimeMillis());
    }

    @Test
    public void getName() {
        String message = String.format("The event name should be %s",
                name);
        assertEquals(message, name, event.getName());
    }

    @Test
    public void getDateCreated() {
        String message = String.format("The event's date_created should not be null");
        assertNotNull(message, event.getDateCreated());
    }

    @Test
    public void getDescription() {
        String message = String.format("The event's description should be %s",
                "" );
        assertEquals(message, event.getDescription(), "");
    }

    @Test
    public void setDescription() {
        String message;
        String description1 = "Good event!";
        String description2 = " ";

        event.setDescription(description1);
        message = String.format("The event's description should not be %s",
                description1 );
        assertEquals(message, event.getDescription(), description1);
        event.setDescription(description2);
        message = String.format("The event's description should not be %s",
                description2);
        assertEquals(message, event.getDescription(), description2);
    }

    @Test
    public void appendDescription() {
        String message, newDescription;

        String currentDescription = event.getDescription();
        event.appendDescription("");
        message = String.format("The event's description should be %s",
                currentDescription );
        assertEquals(message, event.getDescription(), currentDescription);
        newDescription = "! ";
        event.appendDescription(newDescription);
        message = String.format("The event's description should not be %s",
                currentDescription+newDescription);
        assertEquals(message, event.getDescription(), currentDescription+newDescription);
    }

    @Test
    public void getFavorite() {
        String message;
        String newFavorite = "G";

        event.setFavorite(newFavorite);
        message = String.format("The event's favorite should be %s",
                newFavorite );
        assertEquals(message, event.getFavorite(), newFavorite);
    }

    @Test
    public void setFavorite() {
        String message;
        String newFavorite = "";

        event.setFavorite(newFavorite);
        message = String.format("The event's favorite should be %s", newFavorite );
        assertEquals(message, event.getFavorite(), newFavorite);
        newFavorite = "G";
        event.setFavorite(newFavorite);
        message = String.format("The event's favorite should be %s",
                newFavorite );
        assertEquals(message, event.getFavorite(), newFavorite);
    }

    @Test
    public void addTag() {
        String message;

        EventLabelDSO label1 = new EventLabelDSO("a");
        EventLabelDSO label2 = new EventLabelDSO("b", null);
        event.addTag(label1);
        message = String.format("The event should contain %s",
                label1.getName() );
        assertTrue(message, event.getEventTags().contains(label1));
        message = String.format("The event should not contain %s",
                label2.getName() );
        assertFalse(message, event.getEventTags().contains(label2));
        event.addTag(label2);
        message = String.format("The event should contain %s",
                label2.getName() );
        assertTrue(message, event.getEventTags().contains(label2));
    }

    @Test
    public void removeTag() {
        String message;

        EventLabelDSO label1 = new EventLabelDSO("a");
        EventLabelDSO label2 = new EventLabelDSO("b", null);
        event.addTag(label1);
        event.addTag(label2);
        event.removeTag(label1);
        message = String.format("The event should not contain %s",
                label1.getName() );
        assertFalse(message, event.getEventTags().contains(label1));
        message = String.format("The event should contain %s",
                label2.getName() );
        assertTrue(message, event.getEventTags().contains(label2));
    }
}
