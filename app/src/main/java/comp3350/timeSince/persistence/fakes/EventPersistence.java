package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.UserDSO;
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
    public EventDSO getEventByID(String userID, String eventName) throws EventNotFoundException {
        for (int i = 0; i < eventList.size(); i++) {
            EventDSO event = eventList.get(i);
            if (event.getUserID().equals(userID)
                    && event.getName().equals(eventName)) {
                return eventList.get(i);
            }
        }
        throw new EventNotFoundException("The event: " + eventName + " could not be found.");
    }

    @Override
    public EventDSO insertEvent(UserDSO user, EventDSO newEvent) throws DuplicateEventException {
        int index = eventList.indexOf(newEvent);
        if (index < 0) {
            user.addEvent(newEvent);
            eventList.add(newEvent);
            return newEvent;
        } //else: already exists in database
        throw new DuplicateEventException("The event: " + newEvent.getName() + " already exists.");
    }

    @Override
    public EventDSO updateEvent(UserDSO user, EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            user.removeEvent(event); // remove old version
            user.addEvent(event); // add new version
            eventList.set(index, event);
            return event;
        }
        throw new EventNotFoundException("The event: " + event.getName() + " could not be updated.");
    }

    @Override
    public EventDSO deleteEvent(UserDSO user, EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            user.removeEvent(event);
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
    public int numEvents(UserDSO user) {
        // TODO: finish this
        return eventList.size();
    }

}
