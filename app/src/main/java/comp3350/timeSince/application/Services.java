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

    public static synchronized IEventPersistence getEventPersistence(boolean forProduction) {
        if (eventPersistence == null) {
            if (forProduction) {
                eventPersistence = new EventPersistenceHSQLDB(Main.getDBPathName(),
                        getEventLabelPersistence(true));
            } else {
                eventPersistence = new EventPersistence();
            }
        }
        return eventPersistence;
    }

    public static synchronized IEventLabelPersistence getEventLabelPersistence(boolean forProduction) {
        if (eventLabelPersistence == null) {
            if (forProduction) {
                eventLabelPersistence = new EventLabelPersistenceHSQLDB(Main.getDBPathName());
            } else {
                eventLabelPersistence = new EventLabelPersistence();
            }
        }
        return eventLabelPersistence;
    }

    public static synchronized IUserPersistence getUserPersistence(boolean forProduction) {
        if (userPersistence == null) {
            if (forProduction) {
                userPersistence = new UserPersistenceHSQLDB(Main.getDBPathName(),
                        getEventPersistence(true),
                        getEventLabelPersistence(true));
            } else {
                userPersistence = new UserPersistence();
            }
        }
        return userPersistence;
    }

}

