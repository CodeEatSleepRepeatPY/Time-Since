package comp3350.timeSince.tests.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistenceTest {

    private IEventLabelPersistence labelDatabase;
    private EventLabelDSO label1, label2, label3;
    private List<EventLabelDSO> labelList;

    @Before
    public void setUp() {
        labelDatabase = Services.getEventLabelPersistence();
        label1 = new EventLabelDSO("event1");
        label2 = new EventLabelDSO("event2");
        label3 = new EventLabelDSO("event3");
        labelList = new ArrayList<>();
        labelList.add(label2);
        labelList.add(label3);
        labelList.add(label1);
    }

    @After
    public void tearDown() {
        labelDatabase.deleteEventLabel(label1);
        labelDatabase.deleteEventLabel(label2);
        labelDatabase.deleteEventLabel(label3);
        labelDatabase = null;
    }

    @Test
    public void testGetEventLabelList() {

    }

    @Test
    public void testInsertEventLabel() {

    }

    @Test
    public void testUpdateEventLabel() {

    }

    @Test
    public void testDeleteEventLabel() {

    }

    @Test
    public void testNumLabels() {

    }
    
}
