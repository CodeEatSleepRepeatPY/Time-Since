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

    private IEventLabelPersistence eventLabelPersistence;
    private IEventPersistence eventPersistence;
    private IUserPersistence userPersistence;
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
            UserDSO databaseUser = userPersistence.getUserByID(user.getID());
            EventDSO event = new EventDSO(eventName); // create event object with specified name
            EventLabelDSO eventTag = new EventLabelDSO(tagName); // create tag object with specified name

            if(!databaseUser.getUserEvents().contains(event)) {
                event.setID(id++); // set the ID of the event
                event.setTargetFinishTime(dueDate); // set event's due date
                event.addTag(eventTag); // add tag
                //user.getUserEvents().add(event); // add event to user's events list
                databaseUser.getUserEvents().add(event); // add event to user's events list

                eventPersistence.insertEvent(event); // insert event into the database
                eventLabelPersistence.insertEventLabel(eventTag); // insert the newly created event tag into the database

                if (favorite) {
                    event.setFavorite();
                    //user.getFavoritesList().add(event); // add to favourite's list
                    databaseUser.addFavorite(event); // add to favourite's list
                }
            }
        }
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }
}
