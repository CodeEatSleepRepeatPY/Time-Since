package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.utils.DateUtils;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;

    public EventPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private EventDSO fromResultSet(final ResultSet rs) throws SQLException {
        final int id = rs.getInt("eid");
        final String eventName = rs.getString("event_name");
        final Date dateCreated = DateUtils.timestampToDate(rs.getTimestamp("date_created"));
        final String description = rs.getString("description");
        final Date targetFinish = DateUtils.timestampToDate(rs.getTimestamp("target_finish_time"));
        final int timeInterval = rs.getInt("time_interval");
        final boolean isFavorite = rs.getBoolean("is_favorite");
        return new EventDSO(id, eventName, dateCreated, description, targetFinish, timeInterval, isFavorite);
    }

    @Override
    public List<EventDSO> getEventList() {
        final List<EventDSO> events = new ArrayList<>();
        try (final Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM events");
            while (resultSet.next()) {
                final EventDSO event = fromResultSet(resultSet);
                events.add(event);
            }
            resultSet.close();
            statement.close();
            return events;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO getEventByID(String eventID) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("SELECT * FROM events WHERE eid = ?");
            statement.setString(1, eventID);
            ResultSet resultSet = statement.executeQuery();
            EventDSO event = fromResultSet(resultSet);
            resultSet.close();
            statement.close();
            return event;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("INSERT INTO events VALUES(?, ?, ?, ?, ?)");
            statement.setInt(1, newEvent.getID());
            statement.setString(2, newEvent.getName());
            statement.setTimestamp(3, DateUtils.dateToTimestamp(newEvent.getDateCreated()));
            statement.setString(4, newEvent.getDescription());
            statement.setTimestamp(5, DateUtils.dateToTimestamp(newEvent.getTargetFinishTime()));
            statement.setInt(6, newEvent.getFrequency());
            statement.setBoolean(7, newEvent.isFavorite());
            statement.executeUpdate();
            addLabelsConnections(c, newEvent.getEventTags(), newEvent.getID());
            statement.close();
            c.close();
            return newEvent;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO updateEvent(EventDSO event) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("UPDATE events SET event_name = ?, description = ?, target_finish_time = ?, time_interval = ?, is_favorite = ?, WHERE eid = ?");
            statement.setString(1, event.getName());
            statement.setString(2, event.getDescription());
            statement.setTimestamp(3, DateUtils.dateToTimestamp(event.getTargetFinishTime()));
            statement.setInt(4, event.getFrequency());
            statement.setBoolean(5, event.isFavorite());
            addLabelsConnections(c, event.getEventTags(), event.getID());
            return event;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void addLabelsConnections(Connection c, List<EventLabelDSO> labels, int eid) throws SQLException {
        Iterator<EventLabelDSO> iter = labels.iterator();
        while (iter.hasNext()) {
            EventLabelDSO eventLabel = iter.next();
            final PreparedStatement statement = c.prepareStatement("INSERT IGNORE INTO eventslabels VALUES(?, ?)");
            statement.setInt(1, eid);
            statement.setInt(2, eventLabel.getID());
            statement.executeUpdate();
            statement.close();
        }
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) {
        try (final Connection c = connection()) {
            removeLabelsConnections(c, event.getID());
            final PreparedStatement e_statement = c.prepareStatement("DELETE FROM events WHERE eid = ?");
            e_statement.setInt(1, event.getID());
            e_statement.executeUpdate();
            return event;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void removeLabelsConnections(Connection c, int eid) throws SQLException {
        final PreparedStatement userEvents = c.prepareStatement("DELETE FROM eventslabels WHERE eid = ?");
        userEvents.setInt(1, eid);
        userEvents.executeUpdate();
    }

    @Override
    public int numEvents() {
        return getEventList().size();
    }
}


