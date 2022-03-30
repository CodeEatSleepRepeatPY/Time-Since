package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.EventDSO;

public interface IEventPersistence {

    List<EventDSO> getEventList();

    EventDSO getEventByID(int eventID);

    EventDSO insertEvent(EventDSO newEvent);

    EventDSO updateEvent(EventDSO event);

    EventDSO deleteEvent(EventDSO event);

    int numEvents();

    int getNextID();

}
