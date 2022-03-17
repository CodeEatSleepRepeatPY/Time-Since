package comp3350.timeSince.business;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

import java.util.*;

public class EventManager { // TODO: look at accessCourses class in the sample project and all the methods in the event persistence interface, we will need in the event manager

    private IEventPersistence eventDatabase = Services.getEventPersistence();
    private static int count = 0;

    public EventManager(){

    }

    public boolean isDone(EventDSO event){

    }

    public void createOwnEvent(UserDSO user, Date dueDate, String eventName, String tagName, boolean fav){
        EventDSO event = new EventDSO(eventName); // set the name
        EventLabelDSO eventTag = new EventLabelDSO(tagName); // set the tag name

        event.setUuid(count++);
        event.setDueDate(dueDate); // set the due date
        event.addTag(eventTag); // add tag
        eventDatabase.insertEvent(event); // add to user's events
        if(fav) {
            event.setFavorite();
            user.getFavoritesList().add(event); // add to favourite's list
        }
    }
}
