package comp3350.timeSince.application;

import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;
import comp3350.timeSince.persistence.fakes.UserPersistence;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;

public class Services {

    private static IEventPersistence eventPersistence = null;
    private static IEventLabelPersistence eventLabelPersistence = null;
    private static IUserPersistence userPersistence = null;


    public static synchronized IEventPersistence getEventPersistence() {
        if(eventPersistence == null) {
            eventPersistence = new EventPersistence();
        }
        return eventPersistence;
    }

    public static synchronized IEventLabelPersistence getEventLabelPersistence() {
        if(eventLabelPersistence == null) {
            eventLabelPersistence = new EventLabelPersistence();
        }
        return eventLabelPersistence;
    }

    public static synchronized IUserPersistence getUserPersistence() {
        if(userPersistence == null) {
            userPersistence = new UserPersistence();
        }
        return userPersistence;
    }

}

