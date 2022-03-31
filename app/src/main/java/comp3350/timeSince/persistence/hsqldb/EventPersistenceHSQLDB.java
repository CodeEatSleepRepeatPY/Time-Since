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
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.PersistenceException;
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
        final List<EventDSO> events = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                final EventDSO event = fromResultSet(resultSet);
                events.add(event);
            }
            return events;

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("The list of events could not be returned.",
                    e.getMessage());
        }
    }

    @Override
    public EventDSO getEventByID(int eventID) {
        final String query = "SELECT * FROM events WHERE eid = ?";
        EventDSO event = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                event = fromResultSet(resultSet);
            }

            return event;

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException("The event: " + eventID
                    + " could not be found.", e.getMessage());
        }
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) {
        final String query = "INSERT INTO events VALUES(?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            int id = getNextID(); // may cause Persistence Exception
            if (id != -1) {
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

        } catch (PersistenceException | SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("The event: " + newEvent.getName()
                    + " could not be added.", e.getMessage());
        }

        return toReturn;
    }

    @Override
    public EventDSO updateEvent(EventDSO event) {
        final String query = "UPDATE events SET event_name = ?, description = ?, "
                + "target_finish_time = ?, is_favorite = ? "
                + "WHERE eid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, event.getName());
            statement.setString(2, event.getDescription());
            statement.setTimestamp(3, DateUtils.calToTimestamp(event.getTargetFinishTime()));
            statement.setBoolean(4, event.isFavorite());
            statement.executeUpdate();

            addLabelsConnections(c, event.getEventLabels(), event.getID());

            return event;

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException("The event: " + event.getName()
                    + " could not be updated.", e.getMessage());
        }
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) {
        final String query = "DELETE FROM events WHERE eid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            removeLabelsConnections(c, event.getID());

            statement.setInt(1, event.getID());
            statement.executeUpdate();

            return event;

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException("The event: " + event.getName()
                    + " could not be deleted.", e.getMessage());
        }
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
            e.printStackTrace();
            throw new PersistenceException("The number of events could not be calculated.",
                    e.getMessage());
        }

        return toReturn;
    }

    @Override
    public int getNextID() {
        final String query = "SELECT MAX(eid) FROM events";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getRow() + 1;
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("The next event ID could not be identified.",
                    e.getMessage());
        }

        return toReturn;
    }

    private void addLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) throws SQLException {
        final String query = "INSERT IGNORE INTO eventslabels VALUES(?, ?)";

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

    private void connectEventsAndLabels(EventDSO event) {
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
            throw new PersistenceException("Labels could not be added to the event "
                    + event.getName() + ".", e.getMessage());
        }
    }

} //EventPersistenceHSQLDB
