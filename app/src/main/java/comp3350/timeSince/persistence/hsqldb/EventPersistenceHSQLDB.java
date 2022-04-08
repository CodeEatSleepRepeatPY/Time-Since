package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private static final String ID = "eid"; // int
    private static final String NAME = "event_name"; // 30 characters, not null
    private static final String DATE_CREATED = "date_created"; // timestamp, not null
    private static final String DESCRIPTION = "description"; // 100 characters
    private static final String FINISH_TIME = "target_finish_time"; // timestamp
    private static final String YEARS = "frequency_year"; // int
    private static final String MONTHS = "frequency_month"; // int
    private static final String DAYS = "frequency_day"; // int
    private static final String FAVORITE = "is_favorite"; // boolean
    private static final String LABEL_ID = "lid"; // int

    public EventPersistenceHSQLDB(final String dbPath, IEventLabelPersistence eventLabelPersistence) {
        this.dbPath = dbPath;
        this.eventLabelPersistence = eventLabelPersistence;
        nextID = 1;
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

        final int id = rs.getInt(ID);
        final String eventName = rs.getString(NAME);
        final Calendar dateCreated = DateUtils.timestampToCal(rs.getTimestamp(DATE_CREATED));
        final String description = rs.getString(DESCRIPTION);
        final Calendar targetFinish = DateUtils.timestampToCal(rs.getTimestamp(FINISH_TIME));

        EventDSO newEvent = new EventDSO(id, dateCreated, eventName);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);

        connectEventsAndLabels(newEvent);

        return newEvent;
    }

    @Override
    public List<EventDSO> getEventList() {
        final String query = "SELECT * FROM events";
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
            System.out.println("The list of events could not be returned.\n" + e.getMessage() + "\n");
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
    }

    @Override
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        final String query = "SELECT * FROM events WHERE " + ID + " = ?";
        EventDSO toReturn = null;
        final String exceptionMessage = "The event: " + eventID + " could not be found.\n";

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
            throw new EventNotFoundException(exceptionMessage + e.getMessage());
        }

        if (toReturn == null) {
            throw new EventNotFoundException(exceptionMessage);
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        final String query = "INSERT INTO events VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        if (newEvent != null) {
            final String exceptionMessage = "The event: " + newEvent.getName()
                    + " could not be added.\n";

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
                throw new DuplicateEventException(exceptionMessage + e.getMessage());
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
        final String query = "UPDATE events SET " + NAME + " = ?, " + DESCRIPTION + " = ?, "
                + FINISH_TIME + " = ?, " + YEARS + " = ?, " + MONTHS + " = ?, " + DAYS + " = ? "
                + "WHERE " + ID + " = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.\n";

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
                throw new EventNotFoundException(exceptionMessage + e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM events WHERE " + ID + " = ?";
        EventDSO toReturn = null;

        if (event != null) {
            String exceptionMessage = "The event: " + event.getName()
                    + " could not be deleted.\n";

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
                throw new EventNotFoundException(exceptionMessage + e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public int numEvents() {
        final String query = "SELECT COUNT(*) AS numEvents FROM events";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getInt("numEvents");
            }

        } catch (final SQLException e) {
            System.out.println("The number of events could not be calculated.\n" + e.getMessage());
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    @Override
    public int getNextID() {
        return nextID;
    }

    /**
     * @param c      Connection to the database.
     * @param labels List of Event Label objects to add to the database.
     * @param eid    The unique (positive integer) ID of the Event associated with the labels.
     */
    private void addLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) {
        final String queryInsert = "INSERT INTO eventslabels VALUES(?, ?)";

        if (labels != null) {
            for (EventLabelDSO eventLabel : labels) {
                try {
                    try {
                        eventLabelPersistence.insertEventLabel(eventLabel);
                    } catch (DuplicateEventLabelException e) {
                        e.printStackTrace();
                        break;
                    }

                    final PreparedStatement statement = c.prepareStatement(queryInsert);
                    statement.setInt(1, eid);
                    statement.setInt(2, eventLabel.getID());
                    statement.executeUpdate();

                } catch (final SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void removeLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) {
        final String searchQuery = "SELECT " + LABEL_ID + " FROM eventslabels WHERE " + ID + " = ?";
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
        final String query = "SELECT * FROM eventslabels WHERE " + ID + " = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt(LABEL_ID);
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
                    + event.getName() + ".", e.getMessage());
        }
    }

} //EventPersistenceHSQLDB
