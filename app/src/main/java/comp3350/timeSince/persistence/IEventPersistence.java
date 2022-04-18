package comp3350.timeSince.persistence;

import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public interface IEventPersistence {

    boolean eventExists(EventDSO event);

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
     * @param newName The new name of the event.
     * @return The updated event object.
     * @throws EventNotFoundException If the event is not found in the database.
     */
    EventDSO updateEventName(EventDSO event, String newName) throws EventNotFoundException;

    /**
     * @param event The Event object to be updated in the database.
     * @param newDescription The new description of the event.
     * @return The updated event object.
     * @throws EventNotFoundException If the event is not found in the database.
     */
    EventDSO updateEventDescription(EventDSO event, String newDescription) throws EventNotFoundException;

    /**
     * @param event The Event object to be updated in the database.
     * @param newDate The new Calendar date for the target finish time of the event.
     * @return The updated event object.
     * @throws EventNotFoundException If the event is not found in the database.
     */
    EventDSO updateEventFinishTime(EventDSO event, Calendar newDate) throws EventNotFoundException;

    /**
     * Sets status of the event for the user.
     *
     * @param event      the event
     * @param isComplete mark it as complete (true) or incomplete (false)?
     * @return the updated user
     * @throws EventNotFoundException If the Event is not found in the database.
     */
    EventDSO updateEventStatus(EventDSO event, boolean isComplete) throws EventNotFoundException;

    EventDSO updateEventFavorite(EventDSO event, boolean isFavorite) throws EventNotFoundException;

    /**
     * @param event The event object to add a label too.
     * @param label The label object to add to the event.
     * @return The updated event object.
     * @throws EventNotFoundException If the event is not found in the database.
     * @throws EventLabelNotFoundException If the label is not found in the database.
     */
    EventDSO addLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException, EventLabelNotFoundException;

    /**
     * @param event The event object to remove a label from.
     * @param label The label object to remove from the event.
     * @return The updated event object.
     * @throws EventNotFoundException If the event is not found in the database.
     */
    EventDSO removeLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException;

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
