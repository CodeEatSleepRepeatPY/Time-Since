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

import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;
    private final IEventLabelPersistence eventLabelPersistence;

    public EventPersistenceHSQLDB(final String dbPath, IEventLabelPersistence eventLabelPersistence) {
        this.dbPath = dbPath;
        this.eventLabelPersistence = eventLabelPersistence;
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

        final int id = rs.getInt("eid");
        final String eventName = rs.getString("event_name");
        final Calendar dateCreated = DateUtils.timestampToCal(rs.getTimestamp("date_created"));
        final String description = rs.getString("description");
        final Calendar targetFinish = DateUtils.timestampToCal(rs.getTimestamp("target_finish_time"));
        final int isFavorite = rs.getInt("is_favorite");

        EventDSO newEvent = new EventDSO(id, dateCreated, eventName);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);
        newEvent.setFavorite(isFavorite == 1);

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
        final String query = "SELECT * FROM events WHERE eid = ?";
        EventDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException("The event: " + eventID
                    + " could not be found.\n" + e.getMessage());
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        final String query = "INSERT INTO events VALUES(?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            int id = getNextID(); // may cause Persistence Exception
            if (id != -1 && newEvent != null) {
                statement.setInt(1, id);
                statement.setString(2, newEvent.getName());
                statement.setTimestamp(3, DateUtils.calToTimestamp(newEvent.getDateCreated()));
                statement.setString(4, newEvent.getDescription());
                statement.setTimestamp(5, DateUtils.calToTimestamp(newEvent.getTargetFinishTime()));
                statement.setInt(6, newEvent.isFavorite() ? 1 : 0);
                statement.executeUpdate();

                addLabelsConnections(c, newEvent.getEventLabels(), id);

                toReturn = newEvent;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            String eventName = "";
            if (newEvent != null) {
                eventName = newEvent.getName();
            }
            throw new DuplicateEventException("The event: " + eventName
                    + " could not be added.\n" + e.getMessage());
        }

        return toReturn;
    }

    @Override
    public EventDSO updateEvent(EventDSO event) throws EventNotFoundException {
        final String query = "UPDATE events SET event_name = ?, description = ?, "
                + "target_finish_time = ?, is_favorite = ? "
                + "WHERE eid = ?";

        EventDSO toReturn = null;
        if (event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, event.getName());
                statement.setString(2, event.getDescription());
                statement.setTimestamp(3, DateUtils.calToTimestamp(event.getTargetFinishTime()));
                statement.setBoolean(4, event.isFavorite());
                statement.setInt(5, event.getID());
                statement.executeUpdate();

                addLabelsConnections(c, event.getEventLabels(), event.getID());

                toReturn = event;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException("The event: " + event.getName()
                        + " could not be updated.\n" + e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM events WHERE eid = ?";

        EventDSO toReturn = null;
        if (event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                removeLabelsConnections(c, event.getID());

                statement.setInt(1, event.getID());
                statement.executeUpdate();

                toReturn = event;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException("The event: " + event.getName()
                        + " could not be deleted.\n" + e.getMessage());
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
        final String query = "SELECT MAX(eid) AS max FROM events";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getInt("max") + 1;
            }

        } catch (final SQLException e) {
            System.out.println("The next event ID could not be identified.\n" + e.getMessage());
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    /**
     * @param c      Connection to the database.
     * @param labels List of Event Label objects to add to the database.
     * @param eid    The unique (positive integer) ID of the Event associated with the labels.
     * @throws SQLException Any database / SQL issue.
     */
    private void addLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) throws SQLException {
        final String query = "INSERT INTO eventslabels VALUES(?, ?)";

        try {
            for (EventLabelDSO eventLabel : labels) {
                final PreparedStatement statement = c.prepareStatement(query);
                statement.setInt(1, eid);
                statement.setInt(2, eventLabel.getID());
                statement.executeUpdate();
            }

        } catch (final SQLException e) {
            throw new SQLException("Event: " + eid + "'s labels could not be connected.", e);
        }
    }

    /**
     * @param c   Connection to the database.
     * @param eid The unique (positive integer) ID of the Event.
     * @throws SQLException Any database / SQL issue.
     */
    private void removeLabelsConnections(Connection c, int eid) throws SQLException {
        final String query = "DELETE FROM eventslabels WHERE eid = ?";

        try {
            final PreparedStatement userEvents = c.prepareStatement(query);
            userEvents.setInt(1, eid);
            userEvents.executeUpdate();

        } catch (final SQLException e) {
            throw new SQLException("Event: " + eid + "'s labels could not be disconnected.", e);
        }
    }

    /**
     * Finds all Event Labels stored in the database that
     * are associated with the Event.
     *
     * @param event The Event object to add event label's too.
     */
    private void connectEventsAndLabels(EventDSO event) throws SQLException {
        final String query = "SELECT lid FROM eventslabels WHERE eventslabels.eid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt("lid");
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
