package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public class EventsWithLabelsTest {

    private EventDSO event;
    private UserDSO user;
    private EventLabelDSO label1, label2;

    @Before
    public void setUp() {
        Calendar date = Calendar.getInstance();
        user = new UserDSO("admin", date, "12345");
        event = new EventDSO(user.getID(), "Water Plants", date);
        label1 = new EventLabelDSO(user.getID(), "a");
        label2 = new EventLabelDSO(user.getID(), "b");
    }

    @Test
    public void addLabel() {
        String message;

        event.addLabel(label1);
        message = String.format("The event should contain %s",
                label1.getName());
        assertTrue(message, event.getEventLabels().contains(label1));
        message = String.format("The event should not contain %s",
                label2.getName());
        assertFalse(message, event.getEventLabels().contains(label2));
        event.addLabel(label2);
        message = String.format("The event should contain %s",
                label2.getName());
        assertTrue(message, event.getEventLabels().contains(label2));
    }

    @Test
    public void removeLabel() {
        String message;

        event.addLabel(label1);
        event.addLabel(label2);
        event.removeLabel(label1);
        message = String.format("The event should not contain %s",
                label1.getName());
        assertFalse(message, event.getEventLabels().contains(label1));
        message = String.format("The event should contain %s",
                label2.getName());
        assertTrue(message, event.getEventLabels().contains(label2));
    }

}
