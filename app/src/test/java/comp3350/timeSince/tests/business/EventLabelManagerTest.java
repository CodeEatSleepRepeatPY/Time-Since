package comp3350.timeSince.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import comp3350.timeSince.business.EventLabelManager;
import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;
import comp3350.timeSince.persistence.hsqldb.EventLabelPersistenceHSQLDB;

@FixMethodOrder(MethodSorters.JVM)
public class EventLabelManagerTest {

    private EventLabelManager labelManager;
    private IEventLabelPersistence labelPersistence;
    private EventLabelDSO label1, label2, label3;
    private final int initialLabelCount = InitialDatabaseState.NUM_LABELS;
    private final int id1 = initialLabelCount + 1;
    private final int id2 = initialLabelCount + 2;
    private final int id3 = initialLabelCount + 3;

    @Before
    public void setUp() {
        labelPersistence = mock(EventLabelPersistenceHSQLDB.class);
        labelManager = new EventLabelManager(labelPersistence);

        label1 = new EventLabelDSO(id1, "Label1");
        label2 = new EventLabelDSO(id2, "Label2");
        label3 = new EventLabelDSO(id3, "Label3");
    }

    @Test
    public void testGetLabelByID() {
        when(labelPersistence.getEventLabelByID(id1)).thenReturn(label1);
        when(labelPersistence.getEventLabelByID(id2)).thenReturn(label2);
        when(labelPersistence.getEventLabelByID(id3)).thenReturn(label3);
        when(labelPersistence.getEventLabelByID(-1)).thenThrow(EventLabelNotFoundException.class);

        assertEquals("id " + id1 + " should return label1", label1, labelManager.getLabelByID(id1));
        assertEquals("id " + id2 + " should return label2", label2, labelManager.getLabelByID(id2));
        assertEquals("id " + id3 + " should return label3", label3, labelManager.getLabelByID(id3));

        verify(labelPersistence).getEventLabelByID(id1);
        verify(labelPersistence).getEventLabelByID(id2);
        verify(labelPersistence).getEventLabelByID(id3);

        assertNull("An invalid id of -1 should return null", labelManager.getLabelByID(-1));
    }

    @Test
    public void testCreateLabel() {
        when(labelPersistence.getNextID()).thenReturn(id1).thenReturn(id2).thenReturn(id3);
        when(labelPersistence.insertEventLabel(any(EventLabelDSO.class)))
                .thenReturn(label1).thenReturn(label2).thenReturn(label3);

        assertEquals("id " + id1 + " should return label1", label1, labelManager.createLabel("Label1"));
        assertEquals("id " + id2 + " should return label2", label2, labelManager.createLabel("Label2"));
        assertEquals("id " + id3 + " should return label3", label3, labelManager.createLabel("Label3"));

        verify(labelPersistence, times(3)).getNextID();
        verify(labelPersistence, times(3)).insertEventLabel(any(EventLabelDSO.class));

        assertNull("An invalid label should return null", labelManager.createLabel(null));
    }

    @Test
    public void testInsertLabel() {
        when(labelPersistence.insertEventLabel(any(EventLabelDSO.class)))
                .thenReturn(label1).thenReturn(label2).thenReturn(null);

        assertEquals("Label1 should be returned", label1, labelManager.insertLabel(label1));
        assertEquals("Label2 should be returned", label2, labelManager.insertLabel(label2));
        assertNull("An invalid label should return null", labelManager.insertLabel(null));

        verify(labelPersistence, times(2)).insertEventLabel(any(EventLabelDSO.class));
    }

    @Test (expected = DuplicateEventLabelException.class)
    public void testInsertLabelException() {
        when(labelPersistence.insertEventLabel(label1)).thenReturn(label1);
        assertEquals("Label1 should be returned", label1, labelManager.insertLabel(label1));
        when(labelPersistence.insertEventLabel(label1)).thenThrow(DuplicateEventLabelException.class);

        labelManager.insertLabel(label1);
        verify(labelPersistence, times(2)).insertEventLabel(any(EventLabelDSO.class));
    }

    @Test
    public void testUpdateLabelName() {
        EventLabelDSO label1Updated = new EventLabelDSO(id1, "UpdatedName");

        when(labelPersistence.getEventLabelByID(id1)).thenReturn(label1);

        when(labelPersistence.updateEventLabelName(label1, "UpdatedName")).thenReturn(label1Updated);

        assertEquals("EventLabelManager.updateEventLabelName() should return label1Updated",
                label1Updated, labelManager.updateLabelName(id1, "UpdatedName"));

        verify(labelPersistence).getEventLabelByID(id1);
    }

    @Test
    public void testDeleteLabel() {
        when(labelPersistence.getEventLabelByID(id1)).thenReturn(label1);
        when(labelPersistence.getEventLabelByID(id2)).thenReturn(label2);
        when(labelPersistence.getEventLabelByID(id3)).thenReturn(label3);
        when(labelPersistence.getEventLabelByID(-1)).thenThrow(EventLabelNotFoundException.class);

        when(labelPersistence.deleteEventLabel(any(EventLabelDSO.class)))
                .thenReturn(label1).thenReturn(label2).thenReturn(label3);

        assertEquals("id " + id1 + " should return label1", label1, labelManager.deleteLabel(id1));
        assertEquals("id " + id2 + " should return label2", label2, labelManager.deleteLabel(id2));
        assertEquals("id " + id3 + " should return label3", label3, labelManager.deleteLabel(id3));

        verify(labelPersistence).getEventLabelByID(id1);
        verify(labelPersistence).getEventLabelByID(id2);
        verify(labelPersistence).getEventLabelByID(id3);
        verify(labelPersistence, times(3)).deleteEventLabel(any(EventLabelDSO.class));

        assertNull("An invalid id of -1 should return null", labelManager.getLabelByID(-1));
    }

}
