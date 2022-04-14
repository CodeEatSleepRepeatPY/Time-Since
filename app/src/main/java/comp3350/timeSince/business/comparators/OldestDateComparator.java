package comp3350.timeSince.business.comparators;

import java.util.Comparator;

import comp3350.timeSince.objects.EventDSO;

public class OldestDateComparator implements Comparator<EventDSO> {

    @Override
    public int compare(EventDSO event1, EventDSO event2) {
        int toReturn = 0;
        if (event1.getDateCreated().before(event2.getDateCreated())) {
            toReturn = -1;
        }
        if (event1.getDateCreated().after(event2.getDateCreated())) {
            toReturn = 1;
        }
        return toReturn;
    }

}
