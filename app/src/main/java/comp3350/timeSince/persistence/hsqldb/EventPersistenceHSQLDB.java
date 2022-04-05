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
import comp3350.timeSince.objects.UserDSO;
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

        final String userID = rs.getString("uid");
        final String eventName = rs.getString("event_name");
        final Calendar dateCreated = DateUtils.timestampToCal(rs.getTimestamp("date_created"));
        final String description = rs.getString("description");
        final Calendar targetFinish = DateUtils.timestampToCal(rs.getTimestamp("target_finish_time"));
        final boolean isFavorite = rs.getBoolean("is_favorite");
        final boolean isDone = rs.getBoolean("is_done");

        EventDSO newEvent = new EventDSO(userID, eventName, dateCreated);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);
        newEvent.setFavorite(isFavorite);
        newEvent.setIsDone(isDone);

        //connectEventsAndLabels(userID, newEvent);

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

    public List<EventDSO> getEventList(UserDSO user) {
        final String query = "SELECT * FROM events WHERE uid = ?";
        List<EventDSO> toReturn = null;
        final List<EventDSO> events = new ArrayList<>();

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

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
    public EventDSO getEventByID(String userID, String eventName) throws EventNotFoundException {
        final String query = "SELECT * FROM events WHERE uid = ? AND event_name = ?";
        EventDSO toReturn = null;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            statement.setString(2, eventName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException("The event: " + eventName
                    + " could not be found.\n" + e.getMessage());
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(UserDSO user, EventDSO newEvent) throws DuplicateEventException {
        final String query = "INSERT INTO events VALUES(?, ?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        if (user != null && newEvent != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getID());
                statement.setString(2, newEvent.getName());
                statement.setTimestamp(3, DateUtils.calToTimestamp(newEvent.getDateCreated()));
                statement.setString(4, newEvent.getDescription());
                statement.setTimestamp(5, DateUtils.calToTimestamp(newEvent.getTargetFinishTime()));
                statement.setBoolean(6, newEvent.isFavorite());
                statement.setBoolean(7, newEvent.isDone());
                statement.executeUpdate();

                toReturn = newEvent;

            } catch(SQLException e){
                e.printStackTrace();
                throw new DuplicateEventException("The event: " + newEvent.getName()
                        + " could not be added.\n" + e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEvent(UserDSO user, EventDSO event) throws EventNotFoundException {
        final String query = "UPDATE events SET description = ?, "
                + "target_finish_time = ?, is_favorite = ?, is_done = ? "
                + "WHERE uid = ? AND event_name = ?";

        EventDSO toReturn = null;
        if (event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, event.getDescription());
                statement.setTimestamp(2, DateUtils.calToTimestamp(event.getTargetFinishTime()));
                statement.setBoolean(3, event.isFavorite());
                statement.setBoolean(4, event.isDone());

                statement.setString(5, user.getID());
                statement.setString(6, event.getName());
                statement.executeUpdate();

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
    public EventDSO deleteEvent(UserDSO user, EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM events WHERE uid = ? AND event_name = ?";

        EventDSO toReturn = null;
        if (event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getID());
                statement.setString(2, event.getName());
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

    public int numEvents(UserDSO user) {
        final String query = "SELECT COUNT(*) AS numEvents FROM events WHERE uid = ?";
        int toReturn = -1;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

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

    /**
     * Finds all Event Labels stored in the database that
     * are associated with the Event.
     *
     * @param event The Event object to add event label's too.
     */
    private void connectEventsAndLabels(String userID, EventDSO event) throws SQLException {
        final String query = "SELECT label_name FROM eventslabels WHERE uid = ?, event_name = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            statement.setString(2, event.getName());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt("lid");
                for (EventLabelDSO label : eventLabelPersistence.getEventLabelList()) {
                    if (label.getName().equals(labelID)) {
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
