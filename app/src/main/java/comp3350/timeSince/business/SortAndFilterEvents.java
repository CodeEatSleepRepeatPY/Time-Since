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
import comp3350.timeSince.business.interfaces.ISortAndFilterEvents;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class SortAndFilterEvents implements ISortAndFilterEvents {

    private final IUserPersistence userPersistence;
    private Comparator<EventDSO> sorter;
    private final UserDSO user;

    public SortAndFilterEvents(UserDSO user, boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
        sorter = new NewestDateComparator();
        this.user = user;
    }

    @Override
    public List<EventDSO> getEventsByStatus(UserDSO user, boolean complete) {
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

    @Override
    public List<EventDSO> getEventsByLabel(UserDSO user, EventLabelDSO label) {
        List<EventDSO> toReturn = new ArrayList<>();
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        for (EventDSO event : allEvents) {
            if (event.getEventLabels().contains(label)) {
                toReturn.add(event);
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getEventsByDateCreated(UserDSO user, boolean newestToOldest) {
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (!newestToOldest) {
            sorter = new OldestDateComparator();
        }
        Collections.sort(allEvents, sorter);
        return allEvents;
    }

    @Override
    public List<EventDSO> getEventsAlphabetical(UserDSO user, boolean aToZ) {
        List<EventDSO> allEvents = userPersistence.getAllEvents(user);
        if (aToZ) {
            sorter = new AscendingNameComparator();
        } else {
            sorter = new DescendingNameComparator();
        }
        Collections.sort(allEvents, sorter);
        return allEvents;
    }

}
