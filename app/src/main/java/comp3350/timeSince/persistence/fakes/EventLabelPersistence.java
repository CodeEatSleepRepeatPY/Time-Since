package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistence implements IEventLabelPersistence{
    private List<EventLabelDSO> eventLabels;

    public EventLabelPersistence(){
        this.eventLabels = new ArrayList<EventLabelDSO>();
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        return eventLabels;
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) {
        EventLabelDSO toReturn = null;

        if(newEventLabel != null && !eventLabels.contains(newEventLabel)){
            eventLabels.add(newEventLabel);
            toReturn = newEventLabel;
        }

        return toReturn;
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) {
        return null;
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) {
        return null;
    }

}

