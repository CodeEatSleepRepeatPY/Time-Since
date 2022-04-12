package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistence implements IEventLabelPersistence {

    private final List<EventLabelDSO> eventLabels;
    private static int nextID;

    public EventLabelPersistence() {
        this.eventLabels = new ArrayList<>();
        setDefaults();
        nextID = eventLabels.size(); // number of values in the database at creation
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        return Collections.unmodifiableList(eventLabels);
    }

    public EventLabelDSO getEventLabelByID(int labelID) throws EventLabelNotFoundException {
        for (int i = 0; i < eventLabels.size(); i++) {
            if (eventLabels.get(i).getID() == labelID) {
                return eventLabels.get(i);
            }
        }
        throw new EventLabelNotFoundException("The event label: " + labelID + " could not be found.");
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) throws DuplicateEventLabelException {
        int index = eventLabels.indexOf(newEventLabel);
        if (index < 0) {
            eventLabels.add(newEventLabel);
            nextID++;
            return newEventLabel;
        } // else: duplicate
        throw new DuplicateEventLabelException("The event label: " + newEventLabel.getName()
                + " could not be added.");
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        int index = eventLabels.indexOf(eventLabel);
        if (index >= 0) {
            eventLabels.set(index, eventLabel);
            return eventLabel;
        }
        throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                + " could not be updated.");
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
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

    @Override
    public int getNextID() {
        return nextID + 1;
    }

    private void setDefaults() {
        eventLabels.add(new EventLabelDSO(1, "Kitchen"));
        eventLabels.add(new EventLabelDSO(2, "Bathroom"));
        eventLabels.add(new EventLabelDSO(3, "Bedroom"));
        eventLabels.add(new EventLabelDSO(4, "Car"));
        eventLabels.add(new EventLabelDSO(5, "Fitness"));
        eventLabels.add(new EventLabelDSO(6, "Health"));
    }

}
