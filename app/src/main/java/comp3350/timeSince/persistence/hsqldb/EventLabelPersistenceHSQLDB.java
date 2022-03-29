package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.PersistenceException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class EventLabelPersistenceHSQLDB implements IEventLabelPersistence {

    private final String dbPath;

    public EventLabelPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true",
                "SA", "");
    }

    private EventLabelDSO fromResultSet(final ResultSet rs) throws SQLException {

        final int id = rs.getInt("lid");
        final String labelName = rs.getString("label_name");
        final String color = rs.getString("color");

        EventLabelDSO newLabel = new EventLabelDSO(id, labelName);
        newLabel.setColor(EventLabelDSO.Color.valueOf(color));

        return newLabel;
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        final String query = "SELECT * FROM labels";
        final List<EventLabelDSO> labels = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                final EventLabelDSO label = fromResultSet(resultSet);
                labels.add(label);
            }
            return labels;

        } catch (final SQLException e) {
            throw new PersistenceException("List of event labels could not be returned.");
        }
    }

    @Override
    public EventLabelDSO getEventLabelByID(int labelID) {
        final String query = "SELECT * FROM labels WHERE lid = ?";
        EventLabelDSO label = null;
        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, labelID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                label = fromResultSet(resultSet);
            }
            return label;

        } catch (final SQLException e) {
            throw new EventLabelNotFoundException("The event label: " + labelID + " could not be found.");
        }
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) {
        final String query = "INSERT INTO labels VALUES(DEFAULT, ?, ?)";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, newEventLabel.getName());
            statement.setString(2, newEventLabel.getColor().name());
            statement.executeUpdate();

            return newEventLabel;

        } catch (final SQLException e) {
            throw new PersistenceException("The event label: " + newEventLabel.getName()
                    + " could not be added.");
        }
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) {
        final String query = "UPDATE labels SET label_name = ?, color = ?, WHERE lid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, eventLabel.getName());
            statement.setString(2, eventLabel.getColor().name());
            statement.executeUpdate();

            return eventLabel;

        } catch (final SQLException e) {
            throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                    + " could not be updated.");
        }
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) {
        final String query = "DELETE FROM labels WHERE lid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            removeEventsConnections(c, eventLabel.getID());

            statement.setInt(1, eventLabel.getID());
            statement.executeUpdate();

            return eventLabel;

        } catch (final SQLException e) {
            throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                    + " could not be deleted.");
        }
    }

    @Override
    public int numLabels() {
        final String query = "SELECT count(*) AS numLabels FROM labels";

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            resultSet.next();
            return resultSet.getInt("numLabels");

        } catch (final SQLException e) {
            throw new PersistenceException("The number of event labels could not be calculated.");
        }
    }

    private void removeEventsConnections(Connection c, int lid) throws SQLException {
        final String query = "DELETE FROM eventslabels WHERE lid = ?";

        final PreparedStatement userEvents = c.prepareStatement(query);
        userEvents.setInt(1, lid);
        userEvents.executeUpdate();
    }

} //EventLabelPersistenceHSQLDB
