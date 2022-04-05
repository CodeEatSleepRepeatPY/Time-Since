package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IEventPersistence {

    /**
     * @return List of Events (unmodifiable), null if unsuccessful.
     */
    List<EventDSO> getEventList();

    /**
     * @param userID The unique identifier of the User.
     * @param eventName The name of the Event.
     * @return The Event object associated with the parameters, null otherwise.
     * @throws EventNotFoundException If the Event is not in the database.
     */
    EventDSO getEventByID(String userID, String eventName) throws EventNotFoundException;

    /**
     * @param user The User that the Event belongs to.
     * @param newEvent The Event object to be added to the database.
     * @return The Event object that was added to the database, null otherwise.
     * @throws DuplicateEventException If the Event is already stored in the database.
     */
    EventDSO insertEvent(UserDSO user, EventDSO newEvent) throws DuplicateEventException;

    /**
     * @param user The User that the Event belongs to.
     * @param event The Event object to be updated in the database.
     * @return The Event object that was modified, null otherwise.
     * @throws EventNotFoundException If the Event is not found in the database.
     */
    EventDSO updateEvent(UserDSO user, EventDSO event) throws EventNotFoundException;

    /**
     * @param user The User the Event belongs to.
     * @param event The Event object to be deleted from the database.
     * @return The Event object that was deleted, null otherwise.
     * @throws EventNotFoundException If the Event is not found in the database.
     */
    EventDSO deleteEvent(UserDSO user, EventDSO event) throws EventNotFoundException;

    /**
     * @return The number of events in the database, -1 otherwise.
     */
    int numEvents();

    /**
     * @return The number of events in the database, -1 otherwise.
     */
    int numEvents(UserDSO user);

}
