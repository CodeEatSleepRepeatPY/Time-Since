package comp3350.timeSince.business.interfaces;

import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface ISortAndFilterEvents {

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

}
