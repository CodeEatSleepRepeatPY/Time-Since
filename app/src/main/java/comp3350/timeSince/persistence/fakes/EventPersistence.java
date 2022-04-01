package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
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
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getID() == eventID) {
                return eventList.get(i);
            }
        }
        throw new EventNotFoundException("The event: " + eventID + " could not be found.");
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        int index = eventList.indexOf(newEvent);
        if (index < 0) {
            eventList.add(newEvent);
            return newEvent;
        } //else: already exists in database
        throw new DuplicateEventException("The event: " + newEvent.getName() + " already exists.");
    }

    @Override
    public EventDSO updateEvent(EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.set(index, event);
            return event;
        }
        throw new EventNotFoundException("The event: " + event.getName() + " could not be updated.");
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.remove(index);
            return event;
        } // else: event is not in list
        throw new EventNotFoundException("The event: " + event.getName() + " could not be deleted.");
    }

    @Override
    public int numEvents() {
        return eventList.size();
    }

    @Override
    public int getNextID() {
        int toReturn = 0;

        for (EventDSO event : eventList) {
            if (event.getID() > toReturn) {
                toReturn = event.getID();
            }
        }

        return toReturn + 1;
    }

}
