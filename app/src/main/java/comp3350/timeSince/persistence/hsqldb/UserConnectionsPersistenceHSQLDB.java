package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserConnectionsPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserConnectionsPersistenceHSQLDB implements IUserConnectionsPersistence {

    private final String dbPath;
    private final IUserPersistence userPersistence;
    private final IEventPersistence eventPersistence;
    private final IEventLabelPersistence eventLabelPersistence;

    public UserConnectionsPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        this.userPersistence = Services.getUserPersistence(true);
        this.eventPersistence = Services.getEventPersistence(true);
        this.eventLabelPersistence = Services.getEventLabelPersistence(true);
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true",
                "SA", "");
    }

    @Override
    public List<EventDSO> getAllEvents(UserDSO user) {
        final String query = "SELECT " + TS.EVENT_ID + " FROM " + TS.TABLE_USER_EVENTS
                + " WHERE " + TS.USER_ID + " = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(
                                resultSet.getInt(TS.EVENT_ID));
                        toReturn.add(event);
                    } catch (EventNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventLabelDSO> getAllLabels(UserDSO user) {
        final String query = "SELECT " + TS.LABEL_ID + " FROM " + TS.TABLE_USER_LABELS
                + " WHERE " + TS.USER_ID + " = ?";
        List<EventLabelDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(
                                resultSet.getInt(TS.LABEL_ID));
                        toReturn.add(label);
                    } catch (EventLabelNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getFavorites(UserDSO user) {
        final String query = "SELECT " + TS.EVENT_ID + " FROM " + TS.TABLE_USER_EVENTS + " WHERE "
                + TS.USER_ID + " = ? AND " + TS.FAVORITE + " = TRUE";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(
                                resultSet.getInt(TS.EVENT_ID));
                        toReturn.add(event);
                    } catch (EventNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getEventsByStatus(UserDSO user, boolean complete) {
        final String query = "SELECT " + TS.EVENT_ID + " FROM " + TS.TABLE_USER_EVENTS + " WHERE "
                + TS.USER_ID + " = ? AND " + TS.COMPLETE + " = " + complete;
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(
                                resultSet.getInt(TS.EVENT_ID));
                        toReturn.add(event);
                    } catch (EventNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getEventsByLabel(UserDSO user, EventLabelDSO label) {
        final String query = "SELECT * FROM usersevents, eventslabels WHERE " +
                "usersevents.eid = eventslabels.eid AND usersevents.uid = ? AND eventslabels.lid = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null && label != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                statement.setInt(2, label.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(resultSet.getInt(
                                "eventslabels.eid"));
                        toReturn.add(event);
                    } catch (SQLException e) {
                        //e.printStackTrace();
                        break;
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getEventsByDateCreated(UserDSO user, boolean newestToOldest) {
        String order = "ASC";
        if (newestToOldest) {
            order = "DESC";
        }
        final String query = "SELECT * FROM " + TS.TABLE_USER_EVENTS + " WHERE "
                + TS.USER_ID + " = ? ORDER BY " + TS.EVENT_DATE_CREATED + " " + order;
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                final ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    EventDSO event = eventPersistence.getEventByID(
                            resultSet.getInt(TS.EVENT_ID));
                    if (event != null) {
                        toReturn.add(event);
                    }
                }
            } catch (final SQLException e) {
                System.out.println("The events could not be sorted.\n");
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getEventsAlphabetical(UserDSO user, boolean aToZ) {
        String order = aToZ ? "ASC" : "DESC";
        final String query = "SELECT eid, event_name FROM usersevents, events WHERE usersevents" +
                ".eid = events" + ".eid AND usersevents.uid = ? ORDER BY events.event_name " + order;
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    EventDSO event = eventPersistence.getEventByID(resultSet.getInt(TS.EVENT_ID));
                    if (event != null) {
                        toReturn.add(event);
                    }
                }
            } catch (final SQLException e) {
                System.out.println("The events could not be sorted.\n");
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO setStatus(UserDSO user, EventDSO event, boolean isComplete) {
        final String query = "UPDATE " + TS.TABLE_USER_EVENTS + " SET " + TS.COMPLETE + " = "
                + isComplete + " WHERE " + TS.USER_ID + " = ? AND " + TS.EVENT_ID + " = ?";
        UserDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            statement.setInt(2, event.getID());
            int result = statement.executeUpdate();

            if (result > 0) {
                event.setIsDone(isComplete);
                toReturn = user;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public UserDSO addUserEvent(UserDSO user, EventDSO event) {
        UserDSO toReturn = null;
        if (user != null && event != null) {
            try {
                userPersistence.insertUser(user);
            } catch (DuplicateUserException e) {
                System.out.println(e.getMessage());
            } try {
                eventPersistence.insertEvent(event);
            } catch (DuplicateEventException e) {
                System.out.println(e.getMessage());
            }
            user = addUserEventConnection(user, event);
            user = addUserLabelConnections(user, event);
            toReturn = user;
        }
        System.out.println("[LOG] addUserEvent");
        return toReturn;
    }

    private UserDSO addUserEventConnection(UserDSO user, EventDSO event) {
        final String query = "INSERT INTO " + TS.TABLE_USER_EVENTS + " VALUES(?, ?, ?, ?)";

        if (user != null && event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                statement.setInt(2, event.getID());
                statement.setBoolean(3, event.isFavorite());
                statement.setBoolean(4, event.isDone());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.addEvent(event);
                    user = addUserEventLabelConnections(user, event);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[LOG] addUserEventConnection");
        return user;
    }

    private UserDSO addUserEventLabelConnections(UserDSO user, EventDSO event) {

        if (user != null && event != null && event.getEventLabels().size() > 0) {
            List<EventLabelDSO> labels = event.getEventLabels();
            for (EventLabelDSO label : labels) {
                try {
                    eventLabelPersistence.insertEventLabel(label);
                } catch (DuplicateEventLabelException e) {
                    break;
                }
                eventPersistence.addLabel(event, label);
                user = addUserLabel(user, label);
                user.addLabel(label);
            }
        }
        System.out.println("[LOG] addUserEventLabelConnections");
        return user;
    }

    @Override
    public UserDSO removeUserEvent(UserDSO user, EventDSO event) {
        final String query = "DELETE FROM " + TS.TABLE_EVENT + " WHERE " + TS.EVENT_ID + " = ?";

        if (user != null && event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.removeEvent(event);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public UserDSO addUserLabel(UserDSO user, EventLabelDSO label) {
        UserDSO toReturn = null;
        if (user != null && label != null) {
            try {
                userPersistence.insertUser(user);
            } catch (DuplicateUserException e) {
                System.out.println(e.getMessage());
            } try {
                eventLabelPersistence.insertEventLabel(label);
            } catch (DuplicateEventLabelException e) {
                System.out.println(e.getMessage());
            }
            toReturn = addUserLabelConnection(user, label);
        }
        return toReturn;
    }

    private UserDSO addUserLabelConnection(UserDSO user, EventLabelDSO label) {
        final String query = "INSERT INTO " + TS.TABLE_USER_LABELS + " VALUES(?, ?)";

        if (user != null && label != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                statement.setInt(2, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.addLabel(label);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    private UserDSO addUserLabelConnections(UserDSO user, EventDSO event) {
        if (user != null && event != null) {
            List<EventLabelDSO> labels = event.getEventLabels();
            for (EventLabelDSO label : labels) {
                user = addUserLabel(user, label);
                eventPersistence.addLabel(event, label);
            }
        }
        return user;
    }

    @Override
    public UserDSO removeUserLabel(UserDSO user, EventLabelDSO label) {
        final String query = "DELETE FROM " + TS.TABLE_LABEL + " WHERE " + TS.LABEL_ID + " = ?";

        if (user != null && label != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.removeLabel(label);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public UserDSO addFavorite(UserDSO user, EventDSO event) {
        UserDSO toReturn = null;
        if (user != null && event != null) {
//            try {
//                userPersistence.insertUser(user);
//            } catch (DuplicateUserException e) {
//                System.out.println(e.getMessage());
//            } try {
//                eventPersistence.insertEvent(event);
//            } catch (DuplicateEventException e) {
//                System.out.println(e.getMessage());
//            }
            user = addUserEvent(user, event);
            toReturn = addFavoriteConnection(user, event);
        }
        return toReturn;
    }

    private UserDSO addFavoriteConnection(UserDSO user, EventDSO event) {
        final String query = "UPDATE " + TS.TABLE_USER_EVENTS
                + " SET " + TS.FAVORITE + " = TRUE WHERE "
                + TS.USER_ID + " = ? AND " + TS.EVENT_ID + " = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            statement.setInt(2, event.getID());
            int result = statement.executeUpdate();

            if (result > 0) {
                user.addFavorite(event);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public UserDSO removeFavorite(UserDSO user, EventDSO event) {
        UserDSO toReturn = null;
//        if (user != null && event != null) {
//            try {
//                userPersistence.insertUser(user);
//            } catch (DuplicateUserException e) {
//                System.out.println(e.getMessage());
//            } try {
//                eventPersistence.insertEvent(event);
//            } catch (DuplicateEventException e) {
//                System.out.println(e.getMessage());
//            }
            toReturn = removeFavoriteConnection(user, event);
        //}
        return toReturn;
    }

    private UserDSO removeFavoriteConnection(UserDSO user, EventDSO event) {
        final String query = "UPDATE " + TS.TABLE_USER_EVENTS
                + " SET " + TS.FAVORITE + " = FALSE WHERE "
                + TS.USER_ID + " = ? AND " + TS.EVENT_ID + " = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            statement.setInt(2, event.getID());
            int result = statement.executeUpdate();

            if (result > 0) {
                user.removeFavorite(event);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

}
