package comp3350.timeSince.business.comparators;

import java.util.Comparator;

import comp3350.timeSince.objects.EventDSO;

public class NewestDateComparator implements Comparator<EventDSO> {

    /**
     * @param event1 NonNull
     * @param event2 NonNull
     * @return An int value: 0 if event1 creation date == event2 creation date <p>
     * 1 if event1 creation date is before event2 creation date (older)  <p>
     * -1 if event1 creation date is after event2 creation date (newer)
     */
    @Override
    public int compare(EventDSO event1, EventDSO event2) {
        assert event1 != null;
        assert event2 != null;
        int toReturn = 0;
        if (event1.getDateCreated().before(event2.getDateCreated())) {
            toReturn = 1;
        }
        if (event1.getDateCreated().after(event2.getDateCreated())) {
            toReturn = -1;
        }
        return toReturn;
    }

}
