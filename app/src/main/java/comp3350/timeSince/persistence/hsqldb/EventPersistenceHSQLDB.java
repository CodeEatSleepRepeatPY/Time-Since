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
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistenceHSQLDB implements IEventPersistence {

    private final String dbPath;
    private final IEventLabelPersistence eventLabelPersistence;
    private int nextID;

    private static final String TABLE_EVENT = "events";
    private static final String EVENT_ID = "eid"; // int
    private static final String EVENT_NAME = "event_name"; // 30 characters, not null
    private static final String EVENT_DATE_CREATED = "date_created"; // timestamp, not null
    private static final String EVENT_DESCRIPTION = "description"; // 100 characters
    private static final String FINISH_TIME = "target_finish_time"; // timestamp
    private static final String TABLE_EVENT_LABELS = "eventslabels"; // table name
    private static final String LABEL_ID = "lid"; // int

    public EventPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        this.eventLabelPersistence = Services.getEventLabelPersistence(true);
        nextID = 8; // number of values in the database at creation
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

        final int id = rs.getInt(EVENT_ID);
        final String eventName = rs.getString(EVENT_NAME);
        final Calendar dateCreated = DateUtils.timestampToCal(rs.getTimestamp(EVENT_DATE_CREATED));
        final String description = rs.getString(EVENT_DESCRIPTION);
        final Calendar targetFinish = DateUtils.timestampToCal(rs.getTimestamp(FINISH_TIME));

        EventDSO newEvent = new EventDSO(id, dateCreated, eventName);
        newEvent.setDescription(description);
        newEvent.setTargetFinishTime(targetFinish);

        return connectEventsAndLabels(newEvent);
    }

    @Override
    public List<EventDSO> getEventList() {
        final String query = "SELECT * FROM " + TABLE_EVENT;
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
        final String query = "SELECT * FROM " + TABLE_EVENT + " WHERE " + EVENT_ID + " = ?";
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
        final String query = "INSERT INTO " + TABLE_EVENT + " VALUES(?, ?, ?, ?, ?)";
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
                    statement.executeUpdate();

                    toReturn = newEvent;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new DuplicateEventException(exceptionMessage);
            }
            if (toReturn == null) {
                throw new DuplicateEventException(exceptionMessage);
            }
        }
        nextID++;
        return toReturn;
    }

    @Override
    public EventDSO updateEventName(EventDSO event, String newName) {
        final String query = "UPDATE " + TABLE_EVENT + " SET " + EVENT_NAME + " = ? "
                + "WHERE " + EVENT_ID + " = ?";
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
        final String query = "UPDATE " + TABLE_EVENT + " SET " + EVENT_DESCRIPTION + " = ? "
                + "WHERE " + EVENT_ID + " = ?";
        EventDSO toReturn = null;

        if (event != null) {
            final String exceptionMessage = "The event: " + event.getName()
                    + " could not be updated.";

            event.setDescription(newDescription); // will throw an exception if it fails

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

    public EventDSO updateEventFinishTime(EventDSO event, Calendar newDate) {
        final String query = "UPDATE " + TABLE_EVENT + " SET " + FINISH_TIME +
                " = ? WHERE " + EVENT_ID + " = ?";
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
    public EventDSO addLabel(EventDSO event, EventLabelDSO label) {
        final String query = "INSERT INTO " + TABLE_EVENT_LABELS + " VALUES(?, ?)";
        EventDSO toReturn = null;

        if (event != null && label != null) {
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
    public EventDSO removeLabel(EventDSO event, EventLabelDSO label) {
        final String query = "DELETE FROM " + TABLE_EVENT_LABELS + " WHERE "
                + EVENT_ID + " = ? AND " + LABEL_ID + " = ?";
        EventDSO toReturn = null;

        if (event != null && label != null) {
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
        return toReturn;
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        final String query = "DELETE FROM " + TABLE_EVENT + " WHERE " + EVENT_ID + " = ?";
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
        final String query = "SELECT COUNT(*) AS numEvents FROM " + TABLE_EVENT;
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
        return nextID + 1;
    }

    /**
     * Finds all Event Labels stored in the database that
     * are associated with the Event.
     *
     * @param event The Event object to add event label's too.
     */
    private EventDSO connectEventsAndLabels(EventDSO event) throws SQLException {
        final String query = "SELECT * FROM " + TABLE_EVENT_LABELS
                + " WHERE " + EVENT_ID + " = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt(LABEL_ID);
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
