package comp3350.timeSince.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

@FixMethodOrder(MethodSorters.JVM)
public class UsersWithEventsTest {

    private UserDSO user;
    private EventDSO event1, event2, event3;
    private EventLabelDSO label1, label2, label3;

    @Before
    public void setUp() {
        Calendar date = Calendar.getInstance();
        user = new UserDSO(1, "admin", date, "12345");
        event1 = new EventDSO(1, date, "Water Plants");
        event2 = new EventDSO(2, date, "New Toothbrush");
        event3 = new EventDSO(3, date, "Wash Sheets");
        label1 = new EventLabelDSO(1, "Bathroom");
        label2 = new EventLabelDSO(2, "Kitchen");
        label3 = new EventLabelDSO(3, "Bedroom");
    }

    @Test
    public void testAddLabel() {
        assertEquals("A User should not have any labels to start",
                0, user.getUserLabels().size());
        user.addLabel(label2);
        user.addLabel(label3);
        assertEquals("User should be able to add unique labels",
                2, user.getUserLabels().size());
        user.addLabel(label2);
        assertEquals("User should not be able to add duplicates",
                2, user.getUserLabels().size());
        assertTrue(user.getUserLabels().contains(label2));
    }

    @Test
    public void testRemoveLabel() {
        user.addLabel(label1);
        user.addLabel(label2);
        assertEquals("User should have 2 labels",
                2, user.getUserLabels().size());

        user.removeLabel(label1);
        assertEquals("User should have 1 label",
                1, user.getUserLabels().size());
        assertFalse("Removed label should no longer exist",
                user.getUserLabels().contains(label1));

        user.removeLabel(label3);
        assertEquals("Removing a label not in the list should do nothing",
                1, user.getUserLabels().size());
        assertTrue("User should still have a label",
                user.getUserLabels().contains(label2));
    }

    @Test
    public void testAddEvent() {
        assertEquals("User should have no events to start",
                0, user.getUserEvents().size());

        user.addEvent(event1);
        assertEquals("User should have one event",
                1, user.getUserEvents().size());
        assertTrue("User should have correct event",
                user.getUserEvents().contains(event1));

        user.addEvent(event2);
        assertEquals("User should have two events",
                2, user.getUserEvents().size());
        assertTrue("User should have correct unique event",
                user.getUserEvents().contains(event1));

        user.addEvent(event1);
        assertEquals("User should have two events, no duplicates",
                2, user.getUserEvents().size());
        assertTrue("User should still have the original duplicate event",
                user.getUserEvents().contains(event1));
    }

    @Test
    public void testRemoveEvent() {
        user.addEvent(event1);
        user.addEvent(event2);
        assertEquals("User should have 2 events",
                2, user.getUserEvents().size());

        user.removeEvent(event1);
        assertEquals("User should have 1 event",
                1, user.getUserEvents().size());
        assertFalse("Removed event should no longer exist",
                user.getUserEvents().contains(event1));

        user.removeEvent(event3);
        assertEquals("Removing an event not in the list should do nothing",
                1, user.getUserEvents().size());
        assertTrue("User should still have an event",
                user.getUserEvents().contains(event2));
    }

    @Test
    public void testAddFavorite() {
        assertEquals("User should have no favorites to start",
                0, user.getFavoritesList().size());

        user.addFavorite(event1);
        assertEquals("User should have one favorite",
                1, user.getFavoritesList().size());
        assertTrue("User should have correct favorite",
                user.getFavoritesList().contains(event1));

        user.addFavorite(event2);
        assertEquals("User should have two favorites",
                2, user.getFavoritesList().size());
        assertTrue("User should have correct unique favorite",
                user.getFavoritesList().contains(event1));

        user.addFavorite(event1);
        assertEquals("User should have two favorites, no duplicates",
                2, user.getFavoritesList().size());
        assertTrue("User should still have the original duplicate favorite",
                user.getFavoritesList().contains(event1));
    }

    @Test
    public void testRemoveFavorite() {
        user.addFavorite(event1);
        user.addFavorite(event2);
        assertEquals("User should have 2 favorites",
                2, user.getFavoritesList().size());

        user.removeFavorite(event1);
        assertEquals("User should have 1 favorite",
                1, user.getFavoritesList().size());
        assertFalse("Removed favorite should no longer exist",
                user.getFavoritesList().contains(event1));

        user.removeFavorite(event3);
        assertEquals("Removing a favorite not in the list should do nothing",
                1, user.getFavoritesList().size());
        assertTrue("User should still have a favorite",
                user.getFavoritesList().contains(event2));
    }

}
