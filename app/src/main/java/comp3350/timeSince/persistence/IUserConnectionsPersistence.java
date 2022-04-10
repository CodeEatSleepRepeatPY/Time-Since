package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IUserConnectionsPersistence {

    List<EventDSO> getAllEvents(UserDSO user);

    List<EventLabelDSO> getAllLabels(UserDSO user);

    List<EventDSO> getFavorites(UserDSO user);

    List<EventDSO> getEventsByStatus(UserDSO user, boolean complete);

    List<EventDSO> getEventsByLabel(UserDSO user, EventLabelDSO label);

    List<EventDSO> getEventsByDateCreated(UserDSO user, boolean newestToOldest);

    List<EventDSO> getEventsAlphabetical(UserDSO user, boolean aToZ);

    UserDSO setStatus(UserDSO user, EventDSO event, boolean isComplete);

    UserDSO addUserEvent(UserDSO user, EventDSO event);

    UserDSO removeUserEvent(UserDSO user, EventDSO event);

    UserDSO addUserLabel(UserDSO user, EventLabelDSO label);

    UserDSO removeUserLabel(UserDSO user, EventLabelDSO label);

    UserDSO addFavorite(UserDSO user, EventDSO event);

    UserDSO removeFavorite(UserDSO user, EventDSO event);

}
