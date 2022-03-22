package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistence implements IEventPersistence {

    private final List<EventDSO> eventList;

    public EventPersistence() {
        this.eventList = new ArrayList<>();
    }

    @Override
    public List<EventDSO> getEventList() {
        return Collections.unmodifiableList(eventList);
    }

    @Override
    public EventDSO getEventByID(int eventID) {
        EventDSO toReturn = null;
        for (int i = 0; i < eventList.size() && toReturn == null; i++) {
            if (eventList.get(i).getID() == eventID) {
                toReturn = eventList.get(i);
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) {
        EventDSO toReturn = null;
        int index = eventList.indexOf(newEvent);
        if (index < 0) {
            eventList.add(newEvent);
            toReturn = newEvent;
        } // else: duplicate
        return toReturn;
    }

    @Override
    public EventDSO updateEvent(EventDSO event) { //what is this method doing?
        EventDSO toReturn = null;
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.set(index, event);
            toReturn = event;
        }
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) {
        EventDSO toReturn = null;
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.remove(index);
            toReturn = event;
        } // else: event is not in list
        return toReturn;
    }

    @Override
    public int numEvents() {
        return eventList.size();
    }
}
