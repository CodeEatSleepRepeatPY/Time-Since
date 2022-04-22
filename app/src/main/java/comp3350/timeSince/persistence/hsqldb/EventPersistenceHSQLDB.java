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

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.DateUtils;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;
    private final IEventLabelPersistence eventLabelPersistence;

    public EventPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        this.eventLabelPersistence = Services.getEventLabelPersistence(true);
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
        final boolean isFavorite = rs.getBoolean("is_favorite");
        final boolean isDone = rs.getBoolean("is_done");

        EventDSO newEvent = new EventDSO(id, dateCreated, eventName);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);
        newEvent.setFavorite(isFavorite);
        newEvent.setIsDone(isDone);

        return connectEventsAndLabels(newEvent);
    }

    @Override
    public boolean eventExists(EventDSO event) {
        try {
            return getEventByID(event.getID()).equals(event);
        } catch (EventNotFoundException e) {
            return false;
        }
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
            System.out.println("The list of events could not be returned.");
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
    }

    @Override
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        final String query = "SELECT * FROM events WHERE eid = ?";
        EventDSO toReturn = null;
        final String exceptionMessage = "The event: " + eventID + " could not be found.";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = connectEventsAndLabels(fromResultSet(resultSet));
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new EventNotFoundException(exceptionMessage);
        }

        if (toReturn == null) {
            throw new EventNotFoundException(exceptionMessage);
        }
        return toReturn;
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        final String query = "INSERT INTO events VALUES(?, ?, ?, ?, ?, ?, ?)";
        EventDSO toReturn = null;

        if (newEvent != null) {
            final String exceptionMessage = "The event: " + newEvent.getName()
                    + " could not be added.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                int id = newEvent.getID();
                if (id != -1) {
                    statement.setInt(1, id);
                    statement.setString(2, newEvent.getName());
                    statement.setTimestamp(3, DateUtils.calToTimestamp(newEvent.getDateCreated()));
                    statement.setString(4, newEvent.getDescription());
                    statement.setTimestamp(5, DateUtils.calToTimestamp(newEvent.getTargetFinishTime()));
                    statement.setBoolean(6, newEvent.isFavorite());
                    statement.setBoolean(7, newEvent.isDone());
                    int result = statement.executeUpdate();
                    if (result > 0) {
                        List<EventLabelDSO> labels = newEvent.getEventLabels();
                        for (EventLabelDSO label : labels) {
                            newEvent = addLabel(newEvent, label);
                        }
                        toReturn = newEvent;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DuplicateEventException(exceptionMessage);
            }
            if (toReturn == null) {
                throw new DuplicateEventException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventName(EventDSO event, String newName) throws EventNotFoundException {
        final String query = "UPDATE events SET event_name = ? WHERE eid = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newName);
                statement.setInt(2, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.setName(newName);
                    toReturn = event;
                } else {
                    throw new EventNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    public EventDSO updateEventDescription(EventDSO event, String newDescription) throws EventDescriptionException {
        final String query = "UPDATE events SET description = ? WHERE eid = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.";

            event.setDescription(newDescription);

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newDescription);
                statement.setInt(2, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = event;
                } else {
                    throw new EventNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    public EventDSO updateEventFinishTime(EventDSO event, Calendar newDate) throws EventNotFoundException {
        final String query = "UPDATE events SET target_finish_time = ? WHERE eid = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setTimestamp(1, DateUtils.calToTimestamp(newDate));
                statement.setInt(2, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.setTargetFinishTime(newDate);
                    toReturn = event;
                } else {
                    throw new EventNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new EventNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventStatus(EventDSO event, boolean isComplete) throws EventNotFoundException {
        final String query = "UPDATE events SET is_done = " + isComplete + " WHERE  eid = ?";
        EventDSO toReturn = null;

        if (eventExists(event)) { // may throw an exception
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.setIsDone(isComplete);
                    toReturn = event;
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        if (toReturn == null) {
            throw new EventNotFoundException("The event is not in the database");
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventFavorite(EventDSO event, boolean isFavorite) throws EventNotFoundException {
        final String query = "UPDATE events SET is_favorite = " + isFavorite + " WHERE  eid = ?";
        EventDSO toReturn = null;

        if (eventExists(event)) { // may throw an exception
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.setFavorite(isFavorite);
                    toReturn = event;
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        if (toReturn == null) {
            throw new EventNotFoundException("The event is not in the database");
        }
        return toReturn;
    }

    @Override
    public EventDSO addLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException, EventLabelNotFoundException {
        final String query = "INSERT INTO eventslabels VALUES(?, ?)";
        EventDSO toReturn = null;

        if (event != null && label != null) {
            if (!eventExists(event)) {
                throw new EventNotFoundException("The event is not in the database");
            }
            if (!eventLabelPersistence.labelExists(label)) {
                label = eventLabelPersistence.insertEventLabel(label);
            }
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, event.getID());
                statement.setInt(2, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    event.addLabel(label);
                    toReturn = event;
                }

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO removeLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException {
        final String query = "DELETE FROM eventslabels WHERE eid = ? AND lid = ?";
        EventDSO toReturn = null;

        if (event != null && label != null) {
            if (eventExists(event)) { // may throw an exception
                try (final Connection c = connection();
                     final PreparedStatement statement = c.prepareStatement(query)) {

                    statement.setInt(1, event.getID());
                    statement.setInt(2, label.getID());
                    int result = statement.executeUpdate();

                    if (result > 0) {
                        event.removeLabel(label);
                        toReturn = event;
                    }

                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM events WHERE eid = ?";
        EventDSO toReturn = null;

        if (event != null) {
            String exceptionMessage = "The event: " + event.getName()
                    + " could not be deleted.";

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
                throw new EventNotFoundException(exceptionMessage);
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
            System.out.println("The number of events could not be calculated.");
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
            e.printStackTrace();
        }
        return toReturn; // will return -1 if unsuccessful
    }

    /**
     * Finds all Event Labels stored in the database that
     * are associated with the Event.
     *
     * @param event The Event object to add event label's too.
     */
    private EventDSO connectEventsAndLabels(EventDSO event) throws SQLException {
        final String query = "SELECT * FROM eventslabels WHERE eid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt("lid");
                List<EventLabelDSO> labels = eventLabelPersistence.getEventLabelList();
                for (EventLabelDSO label : labels) {
                    if (label.getID() == labelID) {
                        event.addLabel(label);
                        break;
                    }
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            throw new SQLException("Labels could not be added to the event "
                    + event.getName() + ".");
        }
        return event;
    }

} //EventPersistenceHSQLDB
