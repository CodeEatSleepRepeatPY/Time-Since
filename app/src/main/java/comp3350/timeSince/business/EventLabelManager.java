package comp3350.timeSince.business;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelManager {

    private final IEventLabelPersistence eventLabelPersistence;

    public EventLabelManager(boolean forProduction) {
        eventLabelPersistence = Services.getEventLabelPersistence(forProduction);
    }

    public EventLabelManager(IEventLabelPersistence labelPersistence) {
        eventLabelPersistence = labelPersistence;
    }

    public EventLabelDSO getLabelByID(int labelID) throws EventLabelNotFoundException {
        EventLabelDSO toReturn = null;
        if (labelID >= 1) {
            toReturn = eventLabelPersistence.getEventLabelByID(labelID); // may cause exception
        }
        return toReturn;
    }

    public EventLabelDSO createLabel(String labelName) {
        EventLabelDSO toReturn = null;
        EventLabelDSO label = new EventLabelDSO(eventLabelPersistence.getNextID(),
                labelName); // create label object with specified name
        if (label.validate()) {
            toReturn = eventLabelPersistence.insertEventLabel(label);
        }
        return toReturn;
    }

    public EventLabelDSO updateLabelName(int labelID, String newName) throws EventLabelNotFoundException {
        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(labelID);
        if (label != null) {
            label = eventLabelPersistence.updateEventLabelName(label, newName);
        }
        return label;
    }

    public EventLabelDSO deleteLabel(int labelID) throws EventLabelNotFoundException {
        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(labelID);
        if (label != null) {
            label = eventLabelPersistence.deleteEventLabel(label);
        }
        return label;
    }

}
