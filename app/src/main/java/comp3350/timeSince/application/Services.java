package comp3350.timeSince.application;

import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;
import comp3350.timeSince.persistence.fakes.UserPersistence;
import comp3350.timeSince.persistence.hsqldb.EventLabelPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.EventPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.UserPersistenceHSQLDB;

public class Services {

    private static IEventPersistence eventPersistence = null;
    private static IEventLabelPersistence eventLabelPersistence = null;
    private static IUserPersistence userPersistence = null;


    public static synchronized IEventPersistence getEventPersistence() {
        if (eventPersistence == null) {
            eventPersistence = new EventPersistence();
            //eventPersistence = new EventPersistenceHSQLDB(Main.getDBPathName());
        }
        return eventPersistence;
    }

    public static synchronized IEventLabelPersistence getEventLabelPersistence() {
        if (eventLabelPersistence == null) {
            eventLabelPersistence = new EventLabelPersistence();
            //eventLabelPersistence = new EventLabelPersistenceHSQLDB(Main.getDBPathName());
        }
        return eventLabelPersistence;
    }

    public static synchronized IUserPersistence getUserPersistence() {
        if (userPersistence == null) {
            userPersistence = new UserPersistence();
            //userPersistence = new UserPersistenceHSQLDB(Main.getDBPathName());
        }
        return userPersistence;
    }

}
