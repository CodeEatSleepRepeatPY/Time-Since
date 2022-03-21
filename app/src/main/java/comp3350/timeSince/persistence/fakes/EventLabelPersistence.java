package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistence implements IEventLabelPersistence {

    private final List<EventLabelDSO> eventLabels;

    public EventLabelPersistence() {
        this.eventLabels = new ArrayList<>();

        insertEventLabel(new EventLabelDSO("Kitchen"));
        insertEventLabel(new EventLabelDSO("Bathroom"));
        insertEventLabel(new EventLabelDSO("Bedroom"));
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        return eventLabels;
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) {
        EventLabelDSO toReturn = null;
        int index = eventLabels.indexOf(newEventLabel);
        if (index < 0) {
            eventLabels.add(newEventLabel);
            toReturn = newEventLabel;
        } // else: duplicate
        return toReturn;
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) {
        EventLabelDSO toReturn = null;
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            eventLabels.set(index, eventLabel);
            toReturn = eventLabel;
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) {
        EventLabelDSO toReturn = null;
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            eventLabels.remove(index);
            toReturn = eventLabel;
        } // else: event is not in list
        return toReturn;
    }

    @Override
    public int numLabels() {
        return eventLabels.size();
    }

}

