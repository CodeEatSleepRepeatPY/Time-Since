package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistenceHSQLDB implements IEventLabelPersistence {

    private final String dbPath;

    public EventLabelPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private EventLabelDSO fromResultSet(final ResultSet rs) throws SQLException {
        final int id = rs.getInt("lid");
        final String labelName = rs.getString("label_name");
        final String color = rs.getString("color");
        return new EventLabelDSO(id, labelName, color);
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        final List<EventLabelDSO> labels = new ArrayList<>();
        try (final Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM labels");
            while (resultSet.next()) {
                final EventLabelDSO label = fromResultSet(resultSet);
                labels.add(label);
            }
            resultSet.close();
            statement.close();
            return labels;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) {
        try (Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("INSERT INTO labels VALUES(?, ?, ?)");
            statement.setInt(1,newEventLabel.getID());
            statement.setString(2, newEventLabel.getName());
            statement.setString(3, newEventLabel.getColor());
            statement.executeUpdate();
            statement.close();
            c.close();
            return newEventLabel;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("UPDATE labels SET label_name = ?, color = ?, WHERE lid = ?");
            statement.setString(1, eventLabel.getName());
            statement.setString(2, eventLabel.getColor());
            statement.executeUpdate();
            statement.close();
            c.close();
            return eventLabel;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) {
        try (final Connection c = connection()) {
            removeEventsConnections(c, eventLabel.getID());
            final PreparedStatement statement = c.prepareStatement("DELETE FROM labels WHERE lid = ?");
            statement.setInt(1, eventLabel.getID());
            statement.executeUpdate();
            return eventLabel;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void removeEventsConnections(Connection c, int lid) throws SQLException {
        final PreparedStatement userEvents = c.prepareStatement("DELETE FROM eventslabels WHERE lid = ?");
        userEvents.setInt(1, lid);
        userEvents.executeUpdate();
    }

    @Override
    public int numLabels() {
        return getEventLabelList().size();
    }

}
