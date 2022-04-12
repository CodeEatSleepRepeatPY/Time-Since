package comp3350.timeSince.business;

import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserConnectionsPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class EventManager {

    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;
    private final IUserPersistence userPersistence;
    private final IUserConnectionsPersistence userConnectionsPersistence;

    /**
     * Used in production.
     */
    public EventManager(boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
        eventPersistence = Services.getEventPersistence(forProduction);
        eventLabelPersistence = Services.getEventLabelPersistence(forProduction);
        userConnectionsPersistence = Services.getUserConnectionsPersistence();
    }

    /**
     * Used for (mock) testing purposes.
     *
     * @param usersDB User database.
     * @param eventDB Event database.
     * @param eventLabelsDB Event Label database.
     */
    public EventManager(IUserPersistence usersDB, IEventPersistence eventDB, IEventLabelPersistence eventLabelsDB) {
        userPersistence = usersDB;
        eventPersistence = eventDB;
        eventLabelPersistence = eventLabelsDB;
        userConnectionsPersistence = null;
    }

    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (eventID >= 1) {
            toReturn = eventPersistence.getEventByID(eventID); // may cause exception
        }
        return toReturn;
    }

    public List<EventDSO> getEventList() {
        return eventPersistence.getEventList();
    }

    public EventDSO insertEvent(String userID, Calendar dueDate, String eventName,
                                   String eventLabelName, String eventDesc, boolean favorite)
            throws UserNotFoundException, DuplicateEventException {

        EventDSO toReturn = null;
        UserDSO databaseUser = initiateUser(userID);

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
                event.setDescription(eventDesc);

                databaseUser.addEvent(event); // add event to user's events list
                databaseUser.addLabel(eventLabel); // add event label to user's event labels list

                // insert event into the database, may cause exception
                event = eventPersistence.insertEvent(event);
                // insert the newly created event label into the database, may cause exception
                eventLabel = eventLabelPersistence.insertEventLabel(eventLabel);

                databaseUser = userConnectionsPersistence.addUserEvent(databaseUser, event);
                databaseUser = userConnectionsPersistence.addUserLabel(databaseUser, eventLabel);

                if (favorite) {
                    databaseUser.addFavorite(event);
                    userConnectionsPersistence.addFavorite(databaseUser, event);
                }

                toReturn = event; // successful
            }
        }
        return toReturn;
    }

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

    public EventDSO updateEventFavorite(boolean fav, int eventID, String userID) throws EventNotFoundException {
        UserDSO user = initiateUser(userID);
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (event != null) {
            if (fav) {
                userConnectionsPersistence.addFavorite(user, event);
            } else {
                userConnectionsPersistence.removeFavorite(user, event);
            }
            event.setFavorite(fav);
        }
        return event;
    }

    public EventDSO deleteEvent(int eventID) throws EventNotFoundException {
        EventDSO toReturn = null;
        EventDSO eventToDelete = eventPersistence.getEventByID(eventID); // may cause exception

        if (eventToDelete != null) {
            toReturn = eventPersistence.deleteEvent(eventToDelete); // may cause exception
        }

        return toReturn;
    }

    public EventDSO markEventAsDone(int userID, int eventID, boolean done) throws EventNotFoundException {
        UserDSO user = userPersistence.getUserByID(userID); // may cause exception
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (user != null && event != null) {
            event.setIsDone(done);
            userConnectionsPersistence.setStatus(user, event, done);
        }
        return event;
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

    public int numEvents() {
        return eventPersistence.numEvents();
    }

    private UserDSO initiateUser(String userID) {
        UserDSO user;
        UserDSO databaseUser = null;
        try {
            databaseUser = userPersistence.getUserByEmail(userID); // may cause exception
        } catch (UserNotFoundException e) {
            System.out.println("CREATING A DEFAULT USER\n" + e.getMessage());
        } finally {
            if (databaseUser != null) {
                user = databaseUser;
            } else {
                user = new UserDSO(-1, null, null, null);
            }
        }
        return user;
    }

}
