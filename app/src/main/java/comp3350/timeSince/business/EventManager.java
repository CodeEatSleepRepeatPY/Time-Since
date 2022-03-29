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

    public EventDSO insertEvent(String name) {
        EventDSO newEvent = new EventDSO(name);
        newEvent.setID(id++);
        return eventPersistence.insertEvent(newEvent);
    }

    public EventDSO insertEvent(int idParam, String eventName, Date DATE_CREATED, String description, Date targetFinishTime, int frequency, boolean isFavorite) {
        EventDSO newEvent = new EventDSO(idParam, eventName, DATE_CREATED, description, targetFinishTime, frequency, isFavorite);
        newEvent.setID(id++);
        return eventPersistence.insertEvent(newEvent);
    }

    public EventDSO updateEvent(EventDSO event) {
        return event == null ? null : eventPersistence.updateEvent(event);
    }

    public EventDSO deleteEvent(EventDSO event) {
        return event == null ? null : eventPersistence.deleteEvent(event);
    }

    public boolean isDone(EventDSO event){
        boolean toReturn = false;
        if(event != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            Date eventDueDate = event.getTargetFinishTime();
            toReturn = currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
        }
        return toReturn;
    }

    public void createOwnEvent(UserDSO user, Date dueDate, String eventName, String tagName, boolean favorite){
        if(user != null) {
            UserDSO databaseUser = userPersistence.getUserByID(user.getID());
            EventDSO event = new EventDSO(eventName); // create event object with specified name
            EventLabelDSO eventTag = new EventLabelDSO(tagName); // create tag object with specified name

            if(databaseUser != null && !databaseUser.getUserEvents().contains(event)) {
                event.setID(id++); // set the ID of the event
                event.setTargetFinishTime(dueDate); // set event's due date
                event.addTag(eventTag); // add tag
                databaseUser.addEvent(event); // add event to user's events list
                databaseUser.addEventLabel(eventTag); // add event label to user's event labels list

                eventPersistence.insertEvent(event); // insert event into the database
                eventLabelPersistence.insertEventLabel(eventTag); // insert the newly created event tag into the database

                if (favorite) {
                    event.setFavorite();
                    databaseUser.addFavorite(event); // add to favourite's list
                }
            }
        }
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }
}
