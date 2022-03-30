package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
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

    /**
     * @param rs The result of the database query.
     * @return An Event Label object with all fields initialized based on stored values in database.
     * @throws SQLException Any database / SQL issue.
     */
    private EventLabelDSO fromResultSet(final ResultSet rs) throws SQLException {

        final int id = rs.getInt("lid");
        final String labelName = rs.getString("label_name");

        return new EventLabelDSO(id, labelName);
    }

    @Override
    public List<EventLabelDSO> getEventLabelList() {
        final String query = "SELECT * FROM labels";
        List<EventLabelDSO> toReturn = null;
        final List<EventLabelDSO> labels = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                final EventLabelDSO label = fromResultSet(resultSet);
                labels.add(label);
            }
            toReturn = labels;

        } catch (final SQLException e) {
            System.out.println("The list of event labels could not be returned.\n" + e.getMessage() + "\n");
            e.printStackTrace();
            // will return null if unsuccessful
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO getEventLabelByID(int labelID) throws EventLabelNotFoundException {
        final String query = "SELECT * FROM labels WHERE lid = ?";
        EventLabelDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, labelID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventLabelNotFoundException("The event label: " + labelID
                    + " could not be found.", e.getMessage());
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) throws DuplicateEventLabelException {
        final String query = "INSERT INTO labels VALUES(?, ?)";
        EventLabelDSO toReturn = null;

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            int id = getNextID(); // may cause Persistence Exception
            if (id != -1) {
                statement.setInt(1, id);
                statement.setString(2, newEventLabel.getName());
                statement.executeUpdate();

                toReturn = newEventLabel;
            }

        } catch (PersistenceException | SQLException e) {
            e.printStackTrace();
            throw new DuplicateEventLabelException("The event label: " + newEventLabel.getName()
                    + " could not be added.", e.getMessage());
        }

        return toReturn;
    }

    @Override
    public EventLabelDSO updateEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        final String query = "UPDATE labels SET label_name = ? WHERE lid = ?";

        EventLabelDSO toReturn = null;
        if (eventLabel != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, eventLabel.getName());
                statement.executeUpdate();

                toReturn = eventLabel;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                        + " could not be updated.", e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        final String query = "DELETE FROM labels WHERE lid = ?";

        EventLabelDSO toReturn = null;
        if (eventLabel != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                removeEventsConnections(c, eventLabel.getID());

                statement.setInt(1, eventLabel.getID());
                statement.executeUpdate();

                toReturn = eventLabel;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventLabelNotFoundException("The event label: " + eventLabel.getName()
                        + " could not be deleted.", e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public int numLabels() {
        final String query = "SELECT COUNT(*) AS numLabels FROM labels";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getInt("numLabels");
            }

        } catch (final SQLException e) {
            System.out.println("The number of event labels could not be calculated.\n" + e.getMessage());
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    @Override
    public int getNextID() {
        final String query = "SELECT MAX(lid) FROM eventslabels";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getRow() + 1;
            }

        } catch (final SQLException e) {
            System.out.println("The next event label ID could not be identified.\n" + e.getMessage());
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    /**
     * @param c Connection to the database.
     * @param lid The unique (positive integer) ID of the Event Label.
     * @throws SQLException Any database / SQL issue.
     */
    private void removeEventsConnections(Connection c, int lid) throws SQLException {
        final String query = "DELETE FROM eventslabels WHERE lid = ?";

        try {
            final PreparedStatement userEvents = c.prepareStatement(query);
            userEvents.setInt(1, lid);
            userEvents.executeUpdate();
        } catch (final SQLException e) {
            throw new SQLException("Events for label: " + lid + " could not be disconnected.", e);
        }
    }

} //EventLabelPersistenceHSQLDB
