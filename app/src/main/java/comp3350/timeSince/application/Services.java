package comp3350.timeSince.application;

import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserConnectionsPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;
import comp3350.timeSince.persistence.fakes.EventPersistence;
import comp3350.timeSince.persistence.fakes.UserPersistence;
import comp3350.timeSince.persistence.hsqldb.EventLabelPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.EventPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.UserConnectionsPersistenceHSQLDB;
import comp3350.timeSince.persistence.hsqldb.UserPersistenceHSQLDB;

/**
 * Access the persistence layer.
 * <p>
 * There are 4 different persistence files: events, event labels,
 * users, and user connections.
 */
public class Services {

    private static IEventPersistence eventPersistence = null;
    private static IEventLabelPersistence eventLabelPersistence = null;
    private static IUserPersistence userPersistence = null;
    private static IUserConnectionsPersistence userEventPersistence = null;

    /**
     * Gets event persistence.
     *
     * @param forProduction if it should return the real or fake database instance
     * @return the event persistence
     */
    public static synchronized IEventPersistence getEventPersistence(boolean forProduction) {
        if (eventPersistence == null) {
            if (forProduction) {
                eventPersistence = new EventPersistenceHSQLDB(Main.getDBPathName());
            } else {
                eventPersistence = new EventPersistence();
            }
        }
        return eventPersistence;
    }

    /**
     * Gets event label persistence.
     *
     * @param forProduction if it should return the real or fake database instance
     * @return the event label persistence
     */
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

    /**
     * Gets user persistence.
     *
     * @param forProduction if it should return the real or fake database instance
     * @return the user persistence
     */
    public static synchronized IUserPersistence getUserPersistence(boolean forProduction) {
        if (userPersistence == null) {
            if (forProduction) {
                userPersistence = new UserPersistenceHSQLDB(Main.getDBPathName());
            } else {
                userPersistence = new UserPersistence();
            }
        }
        return userPersistence;
    }

    /**
     * Gets user connections persistence.
     * <p>
     * There is no "fake" implementation for this, as it was unnecessary for our
     * project, and was added later to make it easier to make specific queries.
     *
     * @return the user connections persistence
     */
    public static synchronized IUserConnectionsPersistence getUserConnectionsPersistence() {
        if (userEventPersistence == null) {
            userEventPersistence = new UserConnectionsPersistenceHSQLDB(Main.getDBPathName());
        }
        return userEventPersistence;
    }

    /**
     * Clean. Will set all tables to null. To be used VERY carefully.
     */
    public static synchronized void clean() {
        eventPersistence = null;
        eventLabelPersistence = null;
        userPersistence = null;
        userEventPersistence = null;
    }

}
