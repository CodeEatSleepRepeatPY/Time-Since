package comp3350.timeSince.business.comparators;

import java.util.Comparator;

import comp3350.timeSince.objects.EventDSO;

public class DescendingNameComparator implements Comparator<EventDSO> {

    @Override
    public int compare(EventDSO event1, EventDSO event2) {
        return event2.getName().compareTo(event1.getName());
    }
}
