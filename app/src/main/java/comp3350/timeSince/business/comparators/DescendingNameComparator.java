package comp3350.timeSince.business.comparators;

import java.util.Comparator;

import comp3350.timeSince.objects.EventDSO;

public class DescendingNameComparator implements Comparator<EventDSO> {

    /**
     * @param event1 NonNull
     * @param event2 NonNull
     * @return An int value: 0 if event1 name == event2 name <p>
     * < 0 if event1 name is later in the alphabet than event2 name  <p>
     * > 0 if event1 name is earlier in the alphabet than event2 name
     */
    @Override
    public int compare(EventDSO event1, EventDSO event2) {
        assert event1 != null;
        assert event2 != null;
        return event2.getName().compareToIgnoreCase(event1.getName());
    }
}
