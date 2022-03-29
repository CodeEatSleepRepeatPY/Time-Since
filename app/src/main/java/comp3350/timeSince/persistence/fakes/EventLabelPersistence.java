package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.PersistenceException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistence implements IEventLabelPersistence {

    private final List<EventLabelDSO> eventLabels;

    public EventLabelPersistence() {
        this.eventLabels = new ArrayList<>();
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        return Collections.unmodifiableList(eventLabels);
    }

    public EventLabelDSO getEventLabelByID(int labelID) {
        for (int i = 0; i < eventLabels.size(); i++) {
            if (eventLabels.get(i).getID() == labelID) {
                return eventLabels.get(i);
            }
        }
        throw new EventLabelNotFoundException("The event label: " + labelID + " could not be found.");
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) {
        int index = eventLabels.indexOf(newEventLabel);
        if (index < 0) {
            eventLabels.add(newEventLabel);
            return newEventLabel;
        } // else: duplicate
        throw new PersistenceException("The event label: " + newEventLabel.getName()
                + " could not be added.");
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) {
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            eventLabels.set(index, eventLabel);
            return eventLabel;
        }
        throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                + " could not be updated.");
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) {
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            eventLabels.remove(index);
            return eventLabel;
        } // else: event is not in list
        throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                + " could not be deleted.");
    }

    @Override
    public int numLabels() {
        return eventLabels.size();
    }

}
