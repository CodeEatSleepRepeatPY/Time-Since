package comp3350.timeSince.business;

import java.util.Calendar;

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
        return toReturn;
    }
}
