package comp3350.timeSince.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.comparators.AscendingNameComparator;
import comp3350.timeSince.business.comparators.DescendingNameComparator;
import comp3350.timeSince.business.comparators.NewestDateComparator;
import comp3350.timeSince.business.comparators.OldestDateComparator;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class EventManager {

//---------------------------------------------------------------------------------------------
//  Instance Variables
//---------------------------------------------------------------------------------------------

    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;
    private final IUserPersistence userPersistence;
    private Comparator<EventDSO> sorter;
    private UserDSO user;

//---------------------------------------------------------------------------------------------
//  Constructors
//---------------------------------------------------------------------------------------------

    /**
     * Used in production.
     */
    public EventManager(String userEmail, boolean forProduction) throws UserNotFoundException {
        userPersistence = Services.getUserPersistence(forProduction);
        eventPersistence = Services.getEventPersistence(forProduction);
        eventLabelPersistence = Services.getEventLabelPersistence(forProduction);
        sorter = new NewestDateComparator();
        user = userPersistence.getUserByEmail(userEmail); // may cause exception;
    }

    /**
     * Used for (mock) testing purposes.
     *
     * @param usersDB User database.
     * @param eventDB Event database.
     * @param eventLabelsDB Event Label database.
     */
    public EventManager(String userEmail, IUserPersistence usersDB, IEventPersistence eventDB, IEventLabelPersistence eventLabelsDB) throws UserNotFoundException {
        userPersistence = usersDB;
        eventPersistence = eventDB;
        eventLabelPersistence = eventLabelsDB;
        sorter = new NewestDateComparator();
        user = userPersistence.getUserByEmail(userEmail); // may cause exception;
    }

//---------------------------------------------------------------------------------------------
//  Display
//---------------------------------------------------------------------------------------------

    public List<EventDSO> sortByName(boolean aToZ) {
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (aToZ) {
            sorter = new AscendingNameComparator();
        } else {
            sorter = new DescendingNameComparator();
        }
        Collections.sort(allEvents, sorter);
        return allEvents;
    }

    public List<EventDSO> sortByDateCreated(boolean newestToOldest) {
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (!newestToOldest) {
            sorter = new OldestDateComparator();
        }
        Collections.sort(allEvents, sorter);
        return allEvents;
    }

    public List<EventDSO> sortByFinishTime(boolean closestToFurthest) {
        return null;
    }

    public List<EventDSO> filterByLabel(int labelID) throws EventLabelNotFoundException {
        List<EventDSO> toReturn = new ArrayList<>();
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(labelID);
        for (EventDSO event : allEvents) {
            if (event.getEventLabels().contains(label)) {
                toReturn.add(event);
            }
        }
        return toReturn;
    }

    public List<EventDSO> filterByStatus(boolean complete) {
        List<EventDSO> toReturn = new ArrayList<>();
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (complete) {
            for (EventDSO event : allEvents) {
                if (event.isDone()) {
                    toReturn.add(event);
                }
            }
        } else {
            for (EventDSO event : allEvents) {
                if (!event.isDone()) {
                    toReturn.add(event);
                }
            }
        }
        return toReturn;
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

    public UserDSO setUser(String userEmail) throws UserNotFoundException {
        user = userPersistence.getUserByEmail(userEmail); // may cause exception;
        return user;
    }

    // TODO: is this more of a create label? could it have a search user labels for the one with same name, and add connection, not just create new
    public EventDSO addLabel(EventDSO event, String labelName) throws EventNotFoundException {
        EventDSO toReturn = null;
        EventLabelDSO label = new EventLabelDSO(eventLabelPersistence.getNextID(),
                labelName); // create label object with specified name
        if (event != null && event.validate() && label.validate()) {
            label = eventLabelPersistence.insertEventLabel(label);
            // add the connection between the event and label
            event = eventPersistence.addLabel(event, label);
            // add event label to user's event labels list
            user = userPersistence.addUserLabel(user, label);
            toReturn = event;
        }
        return toReturn;
    }

    public EventDSO addLabels(EventDSO event, List<String> labelNames) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (labelNames != null) {
            for (String name : labelNames) {
                toReturn = addLabel(event, name);
            }
        }
        return toReturn;
    }

    public EventDSO removeLabel(EventDSO event, int labelID) throws EventNotFoundException, EventLabelNotFoundException {
        EventDSO toReturn = null;
        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(labelID);
        if (event.validate()) {
            label = eventLabelPersistence.deleteEventLabel(label);
            // remove the connection between the event and label
            event = eventPersistence.removeLabel(event, label);
            // remove event label from user's event labels list
            user = userPersistence.removeUserLabel(user, label);
            toReturn = event;
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
            if (fav) {
                userPersistence.addUserFavorite(user, event);
            } else {
                userPersistence.removeUserFavorite(user, event);
            }
            event.setFavorite(fav);
        }
        return event;
    }

    public EventDSO markEventAsDone(int eventID, boolean done) throws EventNotFoundException {
        EventDSO event = eventPersistence.getEventByID(eventID); // may cause exception

        if (user != null && event != null) {
            event.setIsDone(done);
            userPersistence.setEventStatus(user, event, done);
        }
        return event;
    }

    public UserDSO setEventStatus(EventDSO event, boolean complete) {
        UserDSO toReturn = null;
        if (user != null && user.validate() && event != null && event.validate()) {
            toReturn = userPersistence.setEventStatus(user, event, complete);
        }
        return toReturn;
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
            event = eventPersistence.insertEvent(event);
            event.setFavorite(favorite); // set if favorite or not
            event.setTargetFinishTime(dueDate); // set event's due date
            // add connection between the user and the event (adds event to user's events list)
            user = userPersistence.addUserEvent(user, event);
            if (favorite) {
                user = userPersistence.addUserFavorite(user, event);
            }
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
