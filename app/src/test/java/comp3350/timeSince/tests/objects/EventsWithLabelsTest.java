package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class EventsWithLabelsTest {

    private EventDSO event;
    private EventLabelDSO label1, label2;

    @Before
    public void setUp() {
        Calendar date = Calendar.getInstance();
        event = new EventDSO(1, date, "Water Plants");
        label1 = new EventLabelDSO(1, "a");
        label2 = new EventLabelDSO(2, "b");
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
