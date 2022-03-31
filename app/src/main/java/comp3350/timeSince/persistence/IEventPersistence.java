package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;

public interface IEventPersistence {

    /**
     * @return List of Events (unmodifiable), null if unsuccessful.
     */
    List<EventDSO> getEventList();

    /**
     * @param eventID The unique (positive int) ID of the Event.
     * @return The Event object associated with the ID, null otherwise.
     * @throws EventNotFoundException If the Event is not in the database.
     */
    EventDSO getEventByID(int eventID) throws EventNotFoundException;

    /**
     * @param newEvent The Event object to be added to the database.
     * @return The Event object that was added to the database, null otherwise.
     * @throws DuplicateEventException If the Event is already stored in the database.
     */
    EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException;

    /**
     * @param event The Event object to be updated in the database.
     * @return The Event object that was modified, null otherwise.
     * @throws EventNotFoundException If the Event is not found in the database.
     */
    EventDSO updateEvent(EventDSO event) throws EventNotFoundException;

    /**
     * @param event The Event object to be deleted from the database.
     * @return The Event object that was deleted, null otherwise.
     * @throws EventNotFoundException If the Event is not found in the database.
     */
    EventDSO deleteEvent(EventDSO event) throws EventNotFoundException;

    /**
     * @return The number of events in the database, -1 otherwise.
     */
    int numEvents();

    /**
     * @return The next unique ID if successful, -1 otherwise.
     */
    int getNextID();

}
