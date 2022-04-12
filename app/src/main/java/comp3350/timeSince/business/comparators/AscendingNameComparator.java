package comp3350.timeSince.business.comparators;

import java.util.Comparator;

import comp3350.timeSince.objects.EventDSO;

public class AscendingNameComparator implements Comparator<EventDSO> {

    @Override
    public int compare(EventDSO event1, EventDSO event2) {
        return event1.getName().compareTo(event2.getName());
    }
}
