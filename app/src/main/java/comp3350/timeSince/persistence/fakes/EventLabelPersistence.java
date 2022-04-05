package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
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

    public EventLabelDSO getEventLabelByID(String userID, String labelName) throws EventLabelNotFoundException {
        for (int i = 0; i < eventLabels.size(); i++) {
            EventLabelDSO label = eventLabels.get(i);
            if (label.getUserID().equals(userID) && label.getName().equals(labelName)) {
                return eventLabels.get(i);
            }
        }
        throw new EventLabelNotFoundException("The event label: " + labelName + " could not be found.");
    }

    @Override
    public EventLabelDSO insertEventLabel(UserDSO user, EventLabelDSO newEventLabel) throws DuplicateEventLabelException {
        int index = eventLabels.indexOf(newEventLabel);
        if (index < 0) {
            user.addLabel(newEventLabel);
            eventLabels.add(newEventLabel);
            return newEventLabel;
        } // else: duplicate
        throw new DuplicateEventLabelException("The event label: " + newEventLabel.getName()
                + " could not be added.");
    }

    @Override
    public EventLabelDSO deleteEventLabel(UserDSO user, EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            user.removeLabel(eventLabel);
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
