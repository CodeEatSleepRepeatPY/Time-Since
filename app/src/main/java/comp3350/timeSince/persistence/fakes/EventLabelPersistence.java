package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistence implements IEventLabelPersistence {

    private final List<EventLabelDSO> LABELS;
    private static int nextID;

    public EventLabelPersistence() {
        this.LABELS = new ArrayList<>();
        setDefaults();
        nextID = LABELS.size(); // number of values in the database at creation
    }

    @Override
    public boolean labelExists(EventLabelDSO label) {
        try {
            return getEventLabelByID(label.getID()).equals(label);
        } catch (EventLabelNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        return Collections.unmodifiableList(LABELS);
    }

    public EventLabelDSO getEventLabelByID(int labelID) throws EventLabelNotFoundException {
        for (int i = 0; i < LABELS.size(); i++) {
            if (LABELS.get(i).getID() == labelID) {
                return LABELS.get(i);
            }
        } // else: label is not in the database
        throw new EventLabelNotFoundException("The event label: " + labelID + " could not be found.");
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) throws DuplicateEventLabelException {
        int index = LABELS.indexOf(newEventLabel);
        if (index < 0) {
            LABELS.add(newEventLabel);
            nextID++;
            return newEventLabel;
        } // else: already exists in the database
        throw new DuplicateEventLabelException("The event label: " + newEventLabel.getName()
                + " could not be added.");
    }

    @Override
    public EventLabelDSO updateEventLabelName(EventLabelDSO eventLabel, String newName) throws EventLabelNotFoundException {
        int index = LABELS.indexOf(eventLabel);
        if (index >= 0 && newName != null) {
            eventLabel.setName(newName);
            LABELS.set(index, eventLabel);
            return eventLabel;
        } // else: label is not in the database
        throw new EventLabelNotFoundException("The event label: " + eventLabel.getID()
                + " could not be updated.");
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        int index = LABELS.indexOf(eventLabel);
        if (index >= 0) {
            LABELS.remove(index);
            return eventLabel;
        } // else: label is not in the database
        throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                + " could not be deleted.");
    }

    @Override
    public int numLabels() {
        return LABELS.size();
    }

    @Override
    public int getNextID() {
        return nextID + 1;
    }

    private void setDefaults() {
        LABELS.add(new EventLabelDSO(1, "Kitchen"));
        LABELS.add(new EventLabelDSO(2, "Bathroom"));
        LABELS.add(new EventLabelDSO(3, "Bedroom"));
        LABELS.add(new EventLabelDSO(4, "Car"));
        LABELS.add(new EventLabelDSO(5, "Fitness"));
        LABELS.add(new EventLabelDSO(6, "Health"));
        LABELS.add(new EventLabelDSO(7, "Hygiene"));
        LABELS.add(new EventLabelDSO(8, "Addiction"));
        LABELS.add(new EventLabelDSO(9, "Laundry"));
    }

}
