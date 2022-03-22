package comp3350.timeSince.business;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import java.util.*;

public class EventManager {

    private final IEventLabelPersistence eventLabelPersistence;
    private final IEventPersistence eventPersistence;
    private final IUserPersistence userPersistence;
    private static int id = 0;

    public EventManager(){
        userPersistence = Services.getUserPersistence();
        eventPersistence = Services.getEventPersistence();
        eventLabelPersistence = Services.getEventLabelPersistence();
    }

    public EventDSO getEventByID(int eventID) {
        EventDSO toReturn = null;
        if(eventID >= 0 && eventID < id){
            toReturn = eventPersistence.getEventByID(eventID);
        }
        return toReturn;
    }

    public EventDSO insertEvent(EventDSO newEvent) {
        if(newEvent != null) {
            newEvent.setID(id++);
            eventPersistence.insertEvent(newEvent);
        }
        return newEvent;
    }

    public EventDSO updateEvent(EventDSO event) { // this method might be wrong
        if(event != null){
            eventPersistence.updateEvent(event);
        }
        return event;
    }

    public EventDSO deleteEvent(EventDSO event) {
        if(event != null){
            eventPersistence.deleteEvent(event);
        }
        return event;
    }

    public boolean isDone(EventDSO event){
        Date currentDate = new Date(System.currentTimeMillis());
        Date eventDueDate = event.getTargetFinishTime();

        return currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
    }

    public void createOwnEvent(UserDSO user, Date dueDate, String eventName, String tagName, boolean favorite){
        if(user != null) {
            EventDSO event = new EventDSO(eventName); // set the name
            EventLabelDSO eventTag = new EventLabelDSO(tagName); // set the tag name

            event.setID(id++);
            event.setTargetFinishTime(dueDate); // set the due date
            event.addTag(eventTag); // add tag
            eventPersistence.insertEvent(event); // add to user's events
            if (favorite) {
                event.setFavorite();
                userPersistence.getUserByID(user.getID()).addFavorite(event); // add to favourite's list
                //user.getFavoritesList().add(event); // add to favourite's list
            }
        }
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }
}
