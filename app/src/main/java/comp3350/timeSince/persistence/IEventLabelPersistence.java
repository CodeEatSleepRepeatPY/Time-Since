package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.EventLabelDSO;

public interface IEventLabelPersistence {

    List<EventLabelDSO> getEventLabelList();

    EventLabelDSO getEventLabelByID(int labelID);

    EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel);

    EventLabelDSO updateEventLabel(EventLabelDSO eventLabel);

    EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel);

    int numLabels();

}
