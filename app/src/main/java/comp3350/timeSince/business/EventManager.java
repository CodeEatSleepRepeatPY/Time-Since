package comp3350.timeSince.business;

import java.util.Calendar;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class EventManager {

    private final UserDSO user;
    private final IUserPersistence userPersistence;
    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;

    /**
     * Used in production.
     */
    public EventManager(String userID, boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
        eventPersistence = Services.getEventPersistence(forProduction);
        eventLabelPersistence = Services.getEventLabelPersistence(forProduction);
        user = userPersistence.getUserByID(userID);
    }

    /**
     * Used for (mock) testing purposes.
     *
     * @param eventDB       Event database.
     * @param eventLabelsDB Event Label database.
     */
    public EventManager(String userID, IUserPersistence userDB,
                        IEventPersistence eventDB, IEventLabelPersistence eventLabelsDB) {
        userPersistence = userDB;
        eventPersistence = eventDB;
        eventLabelPersistence = eventLabelsDB;
        user = userDB.getUserByID(userID);
    }

    public EventDSO getEventByID(String eventName) throws EventNotFoundException {
        return eventPersistence.getEventByID(user.getID(), eventName); // may cause exception
    }

    public EventDSO insertEvent(Calendar dueDate, String eventName,
                                String eventLabelName, String eventDesc, boolean favorite)
            throws DuplicateEventException {

        EventDSO toReturn = null;

        Calendar calendar = Calendar.getInstance();
        // create event object with specified name
        EventDSO event = new EventDSO(user.getID(), eventName, calendar);
        // create label object with specified name
        EventLabelDSO eventLabel = new EventLabelDSO(user.getID(), eventLabelName);

        if (event.validate() && eventLabel.validate()) {
            event.setTargetFinishTime(dueDate); // set event's due date
            event.addLabel(eventLabel); // add label
            event.setFavorite(favorite); // set if favorite or not
            event.setDescription(eventDesc);

            // insert the newly created event label into the database, may cause exception
            eventLabelPersistence.insertEventLabel(user, eventLabel);
            // insert event into the database, may cause exception
            eventPersistence.insertEvent(user, event);
            toReturn = event; // successful

        }
        return toReturn;
    }

    public EventDSO updateEventName(String newName, String eventName) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (oldEvent != null) {
            oldEvent.setName(newName);
            updatedEvent = eventPersistence.updateEvent(user, oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventDescription(String desc, String eventName) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (oldEvent != null) {
            oldEvent.setDescription(desc);
            updatedEvent = eventPersistence.updateEvent(user, oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFinishTime(Calendar finishTime, String eventName) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(user.getID(),eventName); // may cause exception

        if (oldEvent != null) {
            oldEvent.setTargetFinishTime(finishTime);
            updatedEvent = eventPersistence.updateEvent(user, oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFavorite(boolean fav, String eventName) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (oldEvent != null) {
            oldEvent.setFavorite(fav);
            updatedEvent = eventPersistence.updateEvent(user, oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO deleteEvent(String eventName) throws EventNotFoundException {
        EventDSO toReturn = null;
        EventDSO eventToDelete = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (eventToDelete != null) {
            toReturn = eventPersistence.deleteEvent(user, eventToDelete); // may cause exception
        }

        return toReturn;
    }

    public void markEventAsDone(String eventName, boolean done) throws EventNotFoundException {
        EventDSO event = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (event != null) {
            event.setIsDone(done);
            eventPersistence.updateEvent(user, event);
        }
    }

    public boolean isDone(String eventName) throws EventNotFoundException {
        boolean toReturn = false;
        EventDSO event = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (event != null) {
            toReturn = event.isDone();
        }
        return toReturn;
    }

    public boolean isOverdue(String eventName) throws EventNotFoundException {
        boolean toReturn = false;
        EventDSO event = eventPersistence.getEventByID(user.getID(), eventName); // may cause exception

        if (event != null) {
            Calendar currentDate = Calendar.getInstance();
            Calendar eventDueDate = event.getTargetFinishTime();
            toReturn = currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
        }
        return toReturn;
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }

    public int numEvents(UserDSO user) {
        return eventPersistence.numEvents(user);
    }

    public UserDSO getUser() {
        return user;
    }

}
