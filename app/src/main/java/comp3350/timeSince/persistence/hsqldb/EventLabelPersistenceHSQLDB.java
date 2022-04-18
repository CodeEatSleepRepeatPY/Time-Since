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
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;

public class EventLabelPersistenceHSQLDB implements IEventLabelPersistence {

    private final String dbPath;
    private int nextID;

    public EventLabelPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        nextID = InitialDatabaseState.NUM_LABELS; // number of values in the database at creation
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
    public boolean labelExists(EventLabelDSO label) {
        try {
            return getEventLabelByID(label.getID()).equals(label);
        } catch (EventLabelNotFoundException e) {
            return false;
        }
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
            System.out.println("The list of event labels could not be returned.");
            e.printStackTrace();
            // will return null if unsuccessful
        }

        return toReturn;
    }

    @Override
    public EventLabelDSO getEventLabelByID(int labelID) throws EventLabelNotFoundException {
        final String query = "SELECT * FROM labels WHERE lid = ?";
        EventLabelDSO toReturn = null;
        final String exceptionMessage = "The event label: " + labelID + " could not be found.";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, labelID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventLabelNotFoundException(exceptionMessage);
        }

        if (toReturn == null) {
            throw new EventLabelNotFoundException(exceptionMessage);
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO insertEventLabel(EventLabelDSO newEventLabel) throws DuplicateEventLabelException {
        final String query = "INSERT INTO labels VALUES(?, ?)";
        EventLabelDSO toReturn = null;

        if (newEventLabel != null) {
            final String exceptionMessage = "The event label: " + newEventLabel.getName()
                    + " could not be added.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                int id = newEventLabel.getID();
                if (id != -1) {
                    statement.setInt(1, id);
                    statement.setString(2, newEventLabel.getName());
                    int result = statement.executeUpdate();

                    if (result > 0) {
                        toReturn = newEventLabel;
                    } else {
                        throw new DuplicateEventLabelException(exceptionMessage);
                    }
                }
            } catch (SQLException e) {
                System.out.println("TEST");
                e.printStackTrace();
                throw new DuplicateEventLabelException(exceptionMessage);
            }
        }

        nextID++;
        return toReturn;
    }

    @Override
    public EventLabelDSO updateEventLabelName(EventLabelDSO eventLabel, String newName) throws EventLabelNotFoundException {
        final String query = "UPDATE labels SET label_name = ? WHERE lid = ?";
        EventLabelDSO toReturn = null;

        if (eventLabel != null && newName != null) {
            final String exceptionMessage = "The event label: " + eventLabel.getID()
                    + " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newName);
                statement.setInt(2, eventLabel.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    eventLabel.setName(newName);
                    toReturn = eventLabel;
                } else {
                    throw new EventLabelNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventLabelNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public EventLabelDSO deleteEventLabel(EventLabelDSO eventLabel) throws EventLabelNotFoundException {
        final String query = "DELETE FROM labels WHERE lid = ?";
        EventLabelDSO toReturn = null;

        if (eventLabel != null) {
            String exceptionMessage = "The event label: " + eventLabel.getName()
                    + " could not be deleted.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, eventLabel.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = eventLabel;
                } else {
                    throw new EventLabelNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventLabelNotFoundException(exceptionMessage);
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
            System.out.println("The number of event labels could not be calculated.");
            e.printStackTrace();
            // will return -1 if unsuccessful
        }

        return toReturn;
    }

    @Override
    public int getNextID() {
        return nextID + 1;
    }

} //EventLabelPersistenceHSQLDB
