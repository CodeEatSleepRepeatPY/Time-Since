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
        Date creationTime = new Date(System.currentTimeMillis());
        EventDSO newEvent = new EventDSO(id, creationTime, name);
        EventDSO insertedEvent = eventPersistence.insertEvent(newEvent);

        if(insertedEvent != null){
            id++;
        }

        return insertedEvent;
    }

    public EventDSO updateEventName(String newName, int eventID) {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID);

        if(oldEvent != null) {
            updatedEvent = new EventDSO(eventID, oldEvent.getDateCreated(), newName);
        }

        return oldEvent == null ? null : eventPersistence.updateEvent(updatedEvent);
    }

    public EventDSO updateEventDescription(String desc, int eventID) {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID);
        String name;

        if(oldEvent != null) {
            name = oldEvent.getName();
            updatedEvent = new EventDSO(eventID, oldEvent.getDateCreated(), name);
            updatedEvent.setDescription(desc);
        }

        return oldEvent == null ? null : eventPersistence.updateEvent(updatedEvent);
    }

    public EventDSO updateEventFinishTime(Date finishTime, int eventID) {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID);
        String name;

        if(oldEvent != null) {
            name = oldEvent.getName();
            updatedEvent = new EventDSO(eventID, oldEvent.getDateCreated(), name);
            updatedEvent.setTargetFinishTime(finishTime);
        }

        return oldEvent == null ? null : eventPersistence.updateEvent(updatedEvent);
    }

    public EventDSO updateEventFrequency(int freq, int eventID) {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID);
        String name;

        if(oldEvent != null) {
            name = oldEvent.getName();
            updatedEvent = new EventDSO(eventID, oldEvent.getDateCreated(), name);
            updatedEvent.setFrequency(freq);
        }

        return oldEvent == null ? null : eventPersistence.updateEvent(updatedEvent);
    }

    public EventDSO updateEventFavorite(boolean fav, int eventID) {
        EventDSO updatedEvent = null;
        EventDSO oldEvent = eventPersistence.getEventByID(eventID);
        String name;

        if(oldEvent != null) {
            name = oldEvent.getName();
            updatedEvent = new EventDSO(eventID, oldEvent.getDateCreated(), name);
            updatedEvent.setFavorite(fav);
        }

        return oldEvent == null ? null : eventPersistence.updateEvent(updatedEvent);
    }

    public EventDSO deleteEvent(int eventID) {
        EventDSO eventToDelete = eventPersistence.getEventByID(eventID);
        EventDSO toReturn = null;

        if(eventToDelete != null){
            toReturn = eventPersistence.deleteEvent(eventToDelete);
        }

        return toReturn;
    }

    public boolean isDone(int eventID){
        EventDSO event = eventPersistence.getEventByID(eventID);
        boolean toReturn = false;
        if(event != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            Date eventDueDate = event.getTargetFinishTime();
            toReturn = currentDate.equals(eventDueDate) || currentDate.after(eventDueDate);
        }
        return toReturn;
    }

    public void createOwnEvent(String userID, Date dueDate, String eventName, String eventLabelName, boolean favorite){
        UserDSO databaseUser = userPersistence.getUserByID(userID);
        if(databaseUser != null) {
            Date date = new Date(System.currentTimeMillis());
            EventDSO event = new EventDSO(id, date, eventName); // create event object with specified name
            EventLabelDSO eventLabel = new EventLabelDSO(id, eventLabelName); // create tag object with specified name

            if(!databaseUser.getUserEvents().contains(event)) {
                event.setTargetFinishTime(dueDate); // set event's due date
                event.addLabel(eventLabel); // add tag

                databaseUser.addEvent(event); // add event to user's events list
                databaseUser.addLabel(eventLabel); // add event label to user's event labels list

                eventPersistence.insertEvent(event); // insert event into the database
                eventLabelPersistence.insertEventLabel(eventLabel); // insert the newly created event tag into the database

                if (favorite) {
                    event.setFavorite(true);
                    databaseUser.addFavorite(event); // add to favourite's list
                }

                id++;
            }
        }
    }

    public int numEvents() {
        return eventPersistence.numEvents();
    }
}
