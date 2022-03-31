package comp3350.timeSince.business;

import java.util.Calendar;

import comp3350.timeSince.application.Main;
import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.hsqldb.EventPersistenceHSQLDB;

public class EventManager {

    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;
    private final IUserPersistence userPersistence;

    /**
     * Used in production.
     */
    public EventManager() {
        userPersistence = Services.getUserPersistence();
        eventPersistence = Services.getEventPersistence();
        eventLabelPersistence = Services.getEventLabelPersistence();
    }

    /**
     * TODO: From the rubric, I think this is actually how we are supposed to do it for production as well
     * Used for testing purposes.
     *
     * @param usersDB User database.
     * @param eventDB Event database.
     * @param eventLabelsDB Event Label database.
     */
    public EventManager(IUserPersistence usersDB, IEventPersistence eventDB, IEventLabelPersistence eventLabelsDB) {
        userPersistence = usersDB;
        eventPersistence = eventDB;
        eventLabelPersistence = eventLabelsDB;
    }

    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        return eventPersistence.getEventByID(eventID); // may cause exception
    }

    public EventDSO insertEvent(String name, Calendar calendar) throws DuplicateEventException {
        EventDSO toReturn = null;
        EventDSO newEvent = new EventDSO(eventPersistence.getNextID(), calendar, name);
        if (newEvent.validate()) {
            EventDSO insertedEvent = eventPersistence.insertEvent(newEvent); // may cause exception
            if (insertedEvent != null) {
                toReturn = insertedEvent;
            }
        }
        return toReturn;
    }

    public EventDSO updateEventName(String newName, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            oldEvent.setName(newName);
            updatedEvent = eventPersistence.updateEvent(oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventDescription(String desc, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            oldEvent.setDescription(desc);
            updatedEvent = eventPersistence.updateEvent(oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFinishTime(Calendar finishTime, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            oldEvent.setTargetFinishTime(finishTime);
            updatedEvent = eventPersistence.updateEvent(oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO updateEventFavorite(boolean fav, int eventID) throws EventNotFoundException {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID); // may cause exception

        if (oldEvent != null) {
            oldEvent.setFavorite(fav);
            updatedEvent = eventPersistence.updateEvent(oldEvent); // may cause exception
        }

        return updatedEvent;
    }

    public EventDSO deleteEvent(int eventID) throws EventNotFoundException {
        EventDSO toReturn = null;
        EventDSO eventToDelete = eventPersistence.getEventByID(eventID); // may cause exception

        if (eventToDelete != null) {
            toReturn = eventPersistence.deleteEvent(eventToDelete); // may cause exception
        }

        return toReturn;
    }

    public void markEventAsDone(int eventID, boolean done) throws EventNotFoundException {
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (event != null) {
            event.setIsDone(done);
        }
    }

    public boolean isDone(int eventID) throws EventNotFoundException {
        boolean toReturn = false;
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (event != null) {
            Calendar currentDate = Calendar.getInstance();
            Calendar eventDueDate = event.getTargetFinishTime();
            toReturn = currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
        }
        return toReturn;
    }

    public boolean createOwnEvent(String userID, Calendar dueDate, String eventName,
                                  String eventLabelName, boolean favorite)
            throws UserNotFoundException, DuplicateEventException {

        boolean toReturn = false;
        UserDSO databaseUser = userPersistence.getUserByID(userID); // may cause exception

        if (databaseUser != null) {
            Calendar calendar = Calendar.getInstance();
            EventDSO event = new EventDSO(eventPersistence.getNextID(), calendar,
                    eventName); // create event object with specified name
            EventLabelDSO eventLabel = new EventLabelDSO(eventLabelPersistence.getNextID(),
                    eventLabelName); // create label object with specified name

            if (event.validate() && eventLabel.validate()) {
                event.setTargetFinishTime(dueDate); // set event's due date
                event.addLabel(eventLabel); // add label
                event.setFavorite(favorite); // set if favorite or not

                databaseUser.addEvent(event); // add event to user's events list
                databaseUser.addLabel(eventLabel); // add event label to user's event labels list

                if (favorite) {
                    databaseUser.addFavorite(event);
                }

                // insert event into the database, may cause exception
                eventPersistence.insertEvent(event);
                // insert the newly created event label into the database, may cause exception
                eventLabelPersistence.insertEventLabel(eventLabel);
                toReturn = true; // successful
            }
        }
        return toReturn;
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }
}

