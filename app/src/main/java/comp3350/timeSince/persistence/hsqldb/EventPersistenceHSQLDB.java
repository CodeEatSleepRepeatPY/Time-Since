package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;
    private final IEventLabelPersistence eventLabelPersistence;
    private int nextID;

    public EventPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        this.eventLabelPersistence = Services.getEventLabelPersistence(true);
        nextID = 6;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true",
                "SA", "");
    }

    /**
     * @param rs The result of the database query.
     * @return An Event object with all fields initialized based on stored values in database.
     * @throws SQLException Any database / SQL issue.
     */
    private EventDSO fromResultSet(final ResultSet rs) throws SQLException {

        final int id = rs.getInt(TS.EVENT_ID);
        final String eventName = rs.getString(TS.EVENT_NAME);
        final Calendar dateCreated = DateUtils.timestampToCal(rs.getTimestamp(TS.EVENT_DATE_CREATED));
        final String description = rs.getString(TS.EVENT_DESCRIPTION);
        final Calendar targetFinish = DateUtils.timestampToCal(rs.getTimestamp(TS.FINISH_TIME));

        EventDSO newEvent = new EventDSO(id, dateCreated, eventName);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);

        connectEventsAndLabels(newEvent);

        return newEvent;
    }

    @Override
    public List<EventDSO> getEventList() {
        final String query = "SELECT * FROM " + TS.TABLE_EVENT;
        List<EventDSO> toReturn = null;
        final List<EventDSO> events = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                final EventDSO event = fromResultSet(resultSet);
                events.add(event);
            }
            toReturn = events;

        } catch (final SQLException e) {
            System.out.println("The list of events could not be returned.");
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
    }

    @Override
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        final String query = "SELECT * FROM " + TS.TABLE_EVENT + " WHERE " + TS.EVENT_ID + " = ?";
        EventDSO toReturn = null;
        final String exceptionMessage = "The event: " + eventID + " could not be found.";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
                connectEventsAndLabels(toReturn);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException(exceptionMessage);
        }

        if (toReturn == null) {
            throw new EventNotFoundException(exceptionMessage);
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        final String query = "INSERT INTO " + TS.TABLE_EVENT + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        if (newEvent != null) {
            final String exceptionMessage = "The event: " + newEvent.getName()
                    + " could not be added.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                int id = newEvent.getID();
                if (id != -1) {
                    statement.setInt(1, id);
                    statement.setString(2, newEvent.getName());
                    statement.setTimestamp(3, DateUtils.calToTimestamp(newEvent.getDateCreated()));
                    statement.setString(4, newEvent.getDescription());
                    statement.setTimestamp(5, DateUtils.calToTimestamp(newEvent.getTargetFinishTime()));
                    statement.setInt(6, newEvent.getFrequency()[0]);
                    statement.setInt(7, newEvent.getFrequency()[1]);
                    statement.setInt(8, newEvent.getFrequency()[2]);
                    statement.executeUpdate();

                    addLabelsConnections(c, newEvent.getEventLabels(), id);
                    toReturn = newEvent;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new DuplicateEventException(exceptionMessage);
            }

            if (toReturn == null) {
                throw new DuplicateEventException(exceptionMessage);
            }
        }

        nextID++;
        return toReturn;
    }

    @Override
    public EventDSO updateEvent(EventDSO event) throws EventNotFoundException {
        final String query = "UPDATE " + TS.TABLE_EVENT + " SET " + TS.EVENT_NAME + " = ?, "
                + TS.EVENT_DESCRIPTION + " = ?, " + TS.FINISH_TIME + " = ?, "
                + TS.YEARS + " = ?, " + TS.MONTHS + " = ?, " + TS.DAYS + " = ? "
                + "WHERE " + TS.EVENT_ID + " = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, event.getName());
                statement.setString(2, event.getDescription());
                statement.setTimestamp(3, DateUtils.calToTimestamp(event.getTargetFinishTime()));
                statement.setInt(4, event.getFrequency()[0]);
                statement.setInt(5, event.getFrequency()[1]);
                statement.setInt(6, event.getFrequency()[2]);
                statement.setInt(7, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    removeLabelsConnections(c, event.getEventLabels(), event.getID());
                    addLabelsConnections(c, event.getEventLabels(), event.getID());
                    toReturn = event;
                } else {
                    throw new EventNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO addLabel(EventDSO event, EventLabelDSO label) {
        final String query = "INSERT INTO eventslabels VALUES(?, ?)";
        EventDSO toReturn = null;

        if (event != null && label != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                statement.setInt(2, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.addLabel(label);
                    toReturn = event;
                }

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[LOG] eventPersistence.addLabel");
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM " + TS.TABLE_EVENT + " WHERE " + TS.EVENT_ID + " = ?";
        EventDSO toReturn = null;

        if (event != null) {
            String exceptionMessage = "The event: " + event.getName()
                    + " could not be deleted.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = event;
                } else {
                    throw new EventNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public int numEvents() {
        final String query = "SELECT COUNT(*) AS numEvents FROM " + TS.TABLE_EVENT;
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getInt("numEvents");
            }

        } catch (final SQLException e) {
            System.out.println("The number of events could not be calculated.");
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    @Override
    public int getNextID() {
        return nextID + 1;
    }

    /**
     * @param c      Connection to the database.
     * @param labels List of Event Label objects to add to the database.
     * @param eid    The unique (positive integer) ID of the Event associated with the labels.
     */
    private void addLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) {
        final String queryInsert = "INSERT INTO " + TS.TABLE_EVENT_LABELS + " VALUES(?, ?)";

        if (labels != null) {
            for (EventLabelDSO eventLabel : labels) {
                try {
                    try {
                        eventLabelPersistence.insertEventLabel(eventLabel);
                    } catch (DuplicateEventLabelException e) {
                        //e.printStackTrace();
                        break;
                    }

                    final PreparedStatement statement = c.prepareStatement(queryInsert);
                    statement.setInt(1, eid);
                    statement.setInt(2, eventLabel.getID());
                    statement.executeUpdate();

                } catch (final SQLException e) {
                    //e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void removeLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) {
        final String searchQuery = "SELECT " + TS.LABEL_ID + " FROM " + TS.TABLE_EVENT_LABELS
                + " WHERE " + TS.EVENT_ID + " = ?";
        List<EventLabelDSO> result = new ArrayList<>();

        try {
            final PreparedStatement statement = c.prepareStatement(searchQuery);
            statement.setInt(1, eid);
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.add(eventLabelPersistence.getEventLabelByID(resultSet.getRow()));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        if (labels != null) {
            for (EventLabelDSO label : result) {
                if (!labels.contains(label)) {
                    eventLabelPersistence.deleteEventLabel(label);
                }
            }
        }
    }

    /**
     * Finds all Event Labels stored in the database that
     * are associated with the Event.
     *
     * @param event The Event object to add event label's too.
     */
    private void connectEventsAndLabels(EventDSO event) throws SQLException {
        final String query = "SELECT * FROM " + TS.TABLE_EVENT_LABELS
                + " WHERE " + TS.EVENT_ID + " = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt(TS.LABEL_ID);
                for (EventLabelDSO label : eventLabelPersistence.getEventLabelList()) {
                    if (label.getID() == labelID) {
                        event.addLabel(label);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new SQLException("Labels could not be added to the event "
                    + event.getName() + ".");
        }
    }

} //EventPersistenceHSQLDB
