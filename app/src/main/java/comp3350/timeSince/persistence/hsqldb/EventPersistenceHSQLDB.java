package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;

    public EventPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private EventDSO fromResultSet(final ResultSet rs) throws SQLException {
        final String eventName = rs.getString("name");
        return new EventDSO(eventName);
    }

    private void loadEvents() {

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
            return null;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) {
        try (final Connection c = connection()) {
            return null;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO updateEvent(EventDSO event) {
        try (final Connection c = connection()) {
            return null;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) {
        try (final Connection c = connection()) {
            final PreparedStatement ue_statement = c.prepareStatement("DELETE FROM usersevents WHERE eid = ?");
            ue_statement.setInt(1, event.getID());
            ue_statement.executeUpdate();
            final PreparedStatement e_statement = c.prepareStatement("DELETE FROM events WHERE eid = ?");
            e_statement.setInt(1, event.getID());
            e_statement.executeUpdate();
            return event;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public int numEvents() {
        int events = 0;
        try (final Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM events");
            if (resultSet.next()) {
                resultSet.last();
                events = resultSet.getRow();
            }
            resultSet.close();
            statement.close();
            return events;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }
}


