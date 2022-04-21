package comp3350.timeSince.business;

import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventManager {

//---------------------------------------------------------------------------------------------
//  Instance Variables
//---------------------------------------------------------------------------------------------

    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;

//---------------------------------------------------------------------------------------------
//  Constructors
//---------------------------------------------------------------------------------------------

    /**
     * Used in production.
     */
    public EventManager(boolean forProduction) {
        eventPersistence = Services.getEventPersistence(forProduction);
        eventLabelPersistence = Services.getEventLabelPersistence(forProduction);
    }

    /**
     * Used for (mock) testing purposes.
     *
     * @param eventDB Event database.
     * @param eventLabelsDB Event Label database.
     */
    public EventManager(IEventPersistence eventDB, IEventLabelPersistence eventLabelsDB) {
        eventPersistence = eventDB;
        eventLabelPersistence = eventLabelsDB;
    }

//---------------------------------------------------------------------------------------------
//  Getters
//---------------------------------------------------------------------------------------------

    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (eventID >= 1) {
            toReturn = eventPersistence.getEventByID(eventID); // may cause exception
        }
        return toReturn;
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }

//---------------------------------------------------------------------------------------------
//  Setters
//---------------------------------------------------------------------------------------------

    public EventDSO addLabelsToEvent(EventDSO event, List<EventLabelDSO> labels) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (labels != null) {
            for (EventLabelDSO label : labels) {
                event = addLabelToEvent(event, label); // may throw an exception
            }
            toReturn = event;
        }
        return toReturn;
    }

    public EventDSO addLabelToEvent(EventDSO event, EventLabelDSO label) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null && event.validate() && label.validate()) {
            if (!eventLabelPersistence.labelExists(label)) {
                label = eventLabelPersistence.insertEventLabel(label);
            }
            if (eventPersistence.eventExists(event)) {
                // add the connection between the event and label
                event = eventPersistence.addLabel(event, label);
                toReturn = event;
            } else {
                throw new EventNotFoundException("The event is not in the database.");
            }
        }
        return toReturn;
    }

    public EventDSO removeLabelFromEvent(EventDSO event, EventLabelDSO label) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null && event.validate()) {
            if (eventPersistence.eventExists(event)) {
                // remove the connection between the event and label
                event = eventPersistence.removeLabel(event, label);
                toReturn = event;
            }
        }
        return toReturn;
    }

//---------------------------------------------------------------------------------------------
//  Updates
//---------------------------------------------------------------------------------------------

    public EventDSO updateEventName(String newName, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            updatedEvent = eventPersistence.updateEventName(oldEvent, newName); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventDescription(String desc, int eventID) throws EventNotFoundException, EventDescriptionException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            updatedEvent = eventPersistence.updateEventDescription(oldEvent, desc); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFinishTime(Calendar finishTime, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            updatedEvent = eventPersistence.updateEventFinishTime(oldEvent, finishTime); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFavorite(boolean fav, int eventID) throws EventNotFoundException {
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception
        if (event != null) {
            event = eventPersistence.updateEventFavorite(event, fav);
        }
        return event;
    }

    public EventDSO markEventAsDone(int eventID, boolean done) throws EventNotFoundException {
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception
        if (event != null) {
            event = eventPersistence.updateEventStatus(event, done);
        }
        return event;
    }

//---------------------------------------------------------------------------------------------
//  General
//---------------------------------------------------------------------------------------------

    public EventDSO createEvent(String eventName, String description,
                                Calendar dueDate, boolean favorite)
            throws DuplicateEventException, EventDescriptionException {

        EventDSO toReturn = null;
        EventDSO event = new EventDSO(eventPersistence.getNextID(),
                Calendar.getInstance(), eventName); // create event object with specified name
        event.setDescription(description); // may cause an exception

        if (event.validate()) {
            // insert event into the database, may cause exception
            event.setFavorite(favorite); // set if favorite or not
            event.setTargetFinishTime(dueDate); // set event's due date
            event = eventPersistence.insertEvent(event);
            toReturn = event; // successful
        }
        return toReturn;
    }

    public EventDSO deleteEvent(int eventID) throws EventNotFoundException {
        EventDSO toReturn = null;
        EventDSO eventToDelete = eventPersistence.getEventByID(eventID); // may cause exception

        if (eventToDelete != null) {
            toReturn = eventPersistence.deleteEvent(eventToDelete); // may cause exception
        }

        return toReturn;
    }

    public boolean isDone(int eventID) throws EventNotFoundException {
        boolean toReturn = false;
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception
        if (event != null) {
            toReturn = event.isDone();
        }
        return toReturn;
    }

    public boolean isOverdue(int eventID) throws EventNotFoundException {
        boolean toReturn = false;
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (event != null) {
            Calendar currentDate = Calendar.getInstance();
            Calendar eventDueDate = event.getTargetFinishTime();
            toReturn = currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
        }
        return toReturn;
    }

}
