package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IUserConnectionsPersistence {

    //----------------------------------------
    // getters
    //----------------------------------------

    /**
     * Gets all the events for the user.
     *
     * @param user the user
     * @return the list of events
     */
    List<EventDSO> getAllEvents(UserDSO user);

    /**
     * Gets all labels for the user.
     *
     * @param user the user
     * @return the list of labels
     */
    List<EventLabelDSO> getAllLabels(UserDSO user);

    /**
     * Gets all event favorites for the user.
     *
     * @param user the user
     * @return the list of favorite events
     */
    List<EventDSO> getFavorites(UserDSO user);

    /**
     * Gets events by status for the user.
     *
     * @param user     the user
     * @param complete return in progress events or completed events?
     * @return list of events by status
     */
    List<EventDSO> getEventsByStatus(UserDSO user, boolean complete);

    /**
     * Gets events by label for the user.
     *
     * @param user  the user
     * @param label the label
     * @return list of events with that label for that user.
     */
    List<EventDSO> getEventsByLabel(UserDSO user, EventLabelDSO label);

    /**
     * Gets events by date created for the user.
     *
     * @param user           the user
     * @param newestToOldest return newest to oldest (true) or oldest to newest (false)?
     * @return list of events by date created
     */
    List<EventDSO> getEventsByDateCreated(UserDSO user, boolean newestToOldest);

    /**
     * Gets events in alphabetical order for the user.
     *
     * @param user the user
     * @param aToZ return A to Z (true), or Z to A (false)?
     * @return list of events sorted alphabetically
     */
    List<EventDSO> getEventsAlphabetical(UserDSO user, boolean aToZ);

    //----------------------------------------
    // setters
    //----------------------------------------

    /**
     * Sets status of the event for the user.
     *
     * @param user       the user
     * @param event      the event
     * @param isComplete mark it as complete (true) or incomplete (false)?
     * @return the updated user
     */
    UserDSO setStatus(UserDSO user, EventDSO event, boolean isComplete);

    /**
     * Add an event to the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO addUserEvent(UserDSO user, EventDSO event);

    /**
     * Remove an event from the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO removeUserEvent(UserDSO user, EventDSO event);

    /**
     * Add a label to the user.
     *
     * @param user  the user
     * @param label the label
     * @return the updated user
     */
    UserDSO addUserLabel(UserDSO user, EventLabelDSO label);

    /**
     * Remove label from the user.
     *
     * @param user  the user
     * @param label the label
     * @return the updated user
     */
    UserDSO removeUserLabel(UserDSO user, EventLabelDSO label);

    /**
     * Add a favorite event to the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO addFavorite(UserDSO user, EventDSO event);

    /**
     * Remove a favorite event from the user
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO removeFavorite(UserDSO user, EventDSO event);

}
