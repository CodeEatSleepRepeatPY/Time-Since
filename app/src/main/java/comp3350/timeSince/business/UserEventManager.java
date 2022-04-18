package comp3350.timeSince.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.comparators.AscendingNameComparator;
import comp3350.timeSince.business.comparators.DescendingNameComparator;
import comp3350.timeSince.business.comparators.NewestDateComparator;
import comp3350.timeSince.business.comparators.OldestDateComparator;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserEventManager {

    private final IUserPersistence userPersistence;
    private final IEventPersistence eventPersistence;
    private final IEventLabelPersistence labelPersistence;
    private Comparator<EventDSO> sorter;
    private UserDSO user;

    public UserEventManager(boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
        eventPersistence = Services.getEventPersistence(forProduction);
        labelPersistence = Services.getEventLabelPersistence(forProduction);
        user = new UserDSO(-1, null, null, null); // default, invalid
    }

    public UserEventManager(String userEmail, boolean forProduction) throws UserNotFoundException {
        this(forProduction);
        user = userPersistence.getUserByEmail(userEmail);
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

    public List<EventDSO> sortByDateCreated(boolean recentToOldest) {
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (!recentToOldest) {
            sorter = new OldestDateComparator();
        } else {
            sorter = new NewestDateComparator();
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
        EventLabelDSO label = labelPersistence.getEventLabelByID(labelID);
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

    public List<EventDSO> getUserEvents() {
        List<EventDSO> toReturn = null;
        if (validateUser(user)) {
            toReturn = userPersistence.getAllEvents(user);
        }
        return toReturn;
    }

    public List<EventDSO> getUserFavorites() {
        List<EventDSO> toReturn = null;
        if (validateUser(user)) {
            toReturn = userPersistence.getFavorites(user);
        }
        return toReturn;
    }

    public List<EventLabelDSO> getUserLabels() {
        List<EventLabelDSO> toReturn = null;
        if (validateUser(user)) {
            toReturn = userPersistence.getAllLabels(user);
        }
        return toReturn;
    }

//---------------------------------------------------------------------------------------------
//  Setters
//---------------------------------------------------------------------------------------------

    public UserDSO addUserEvent(EventDSO event) {
        UserDSO toReturn = null;
        if (validateUser(user) && validateEvent(event)) {
            if (!eventPersistence.eventExists(event)) {
                event = eventPersistence.insertEvent(event);
            }
            // add connection between the user and the event (adds event to user's events list)
            toReturn = userPersistence.addUserEvent(user, event);
        }
        return toReturn;
    }

    public UserDSO removeUserEvent(EventDSO event) {
        UserDSO toReturn = null;
        if (validateUser(user)) {
            // remove connection between the user and the event (removes event from user's events list)
            toReturn = userPersistence.removeUserEvent(user, event);
        }
        return toReturn;
    }

    public UserDSO addUserFavorite(EventDSO fav) {
        UserDSO toReturn = null;
        if (validateUser(user) && validateEvent(fav)) {
            if (!eventPersistence.eventExists(fav)) {
                fav = eventPersistence.insertEvent(fav);
            }
            toReturn = userPersistence.addUserFavorite(user, fav);
        }
        return toReturn;
    }

    public UserDSO removeUserFavorite(EventDSO event) {
        UserDSO toReturn = null;
        if (validateUser(user)) {
            event = eventPersistence.updateEventFavorite(event, false);
            user.removeFavorite(event);
            toReturn = user;
        }
        return toReturn;
    }

    public UserDSO addUserLabel(EventLabelDSO label) {
        UserDSO toReturn = null;
        if (validateUser(user) && validateLabel(label)) {
            if (!labelPersistence.labelExists(label)) {
                label = labelPersistence.insertEventLabel(label);
            }
            toReturn = userPersistence.addUserLabel(user, label);
        }
        return toReturn;
    }

    public UserDSO removeUserLabel(EventLabelDSO label) {
        UserDSO toReturn = null;
        if (validateUser(user)) {
            toReturn = userPersistence.removeUserLabel(user, label);
        }
        return toReturn;
    }

//---------------------------------------------------------------------------------------------
//  Helpers
//---------------------------------------------------------------------------------------------

    private boolean validateUser(UserDSO user) {
        return user != null && user.validate();
    }

    private boolean validateEvent(EventDSO event) {
        return event != null && event.validate();
    }

    private boolean validateLabel(EventLabelDSO label) {
        return label != null && label.validate();
    }

}
