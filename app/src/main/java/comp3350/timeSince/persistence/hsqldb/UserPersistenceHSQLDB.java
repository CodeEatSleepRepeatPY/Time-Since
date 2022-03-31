
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
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PersistenceException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserPersistenceHSQLDB implements IUserPersistence {

    private final String dbPath;
    private final IEventPersistence eventPersistence;
    private final IEventLabelPersistence eventLabelPersistence;

    public UserPersistenceHSQLDB(final String dbPath, IEventPersistence eventPersistence,
                                 IEventLabelPersistence eventLabelPersistence) {
        this.dbPath = dbPath;
        this.eventPersistence = eventPersistence;
        this.eventLabelPersistence = eventLabelPersistence;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true",
                "SA", "");
    }

    /**
     * @param rs The result of the database query.
     * @return A User object with all fields initialized based on stored values in database.
     * @throws SQLException Any database / SQL issue.
     */
    private UserDSO fromResultSet(final ResultSet rs) throws SQLException {

        final String uID = rs.getString("uid");
        final String userName = rs.getString("user_name");
        final Calendar dateRegistered = DateUtils.timestampToCal(rs.getTimestamp("date_registered"));
        final String passwordHash = rs.getString("password_hash");

        UserDSO newUser = new UserDSO(uID, dateRegistered, passwordHash);
        newUser.setName(userName);

        connectUsersAndEvents(newUser);
        connectUsersAndFavorites(newUser);
        connectUsersAndLabels(newUser);

        return newUser;
    }

    @Override
    public List<UserDSO> getUserList() {
        final String query = "SELECT * FROM users";
        final List<UserDSO> users = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                UserDSO user = fromResultSet(resultSet);
                users.add(user);
            }
            return users;

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("The list of users could not be returned.", e.getMessage());
        }
    }

    @Override
    public UserDSO getUserByID(String userID) throws UserNotFoundException {
        final String query = "SELECT * FROM users WHERE uid = ?";

        UserDSO toReturn = null;
        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new UserNotFoundException("The user: " + userID
                    + " could not be found.", e.getMessage());
        }
        return toReturn;
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) throws DuplicateUserException {
        final String query = "INSERT INTO users VALUES(?, ?, ?, ?)";

        UserDSO toReturn = null;
        if (newUser != null){
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newUser.getID());
                statement.setString(2, newUser.getName());
                statement.setTimestamp(3, DateUtils.calToTimestamp(newUser.getDateRegistered()));
                statement.setString(4, newUser.getPasswordHash());
                statement.executeUpdate();

                addLabelConnections(c, newUser.getUserLabels(), newUser.getID());
                addEventConnections(c, newUser.getUserEvents(), newUser.getID());

                toReturn = newUser;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new DuplicateUserException("The user: " + newUser.getName()
                        + " could not be added.", e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUser(UserDSO user) throws UserNotFoundException {
        final String query = "UPDATE users SET user_name = ?, password_hash = ? "
                + "WHERE uid = ?";

        UserDSO toReturn = null;
        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getName());
                statement.setString(2, user.getPasswordHash());
                statement.executeUpdate();

                addLabelConnections(c, user.getUserLabels(), user.getID());
                addEventConnections(c, user.getUserEvents(), user.getID());

                toReturn = user;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException("The user: " + user.getName() +
                        " could not be updated.", e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) throws UserNotFoundException {
        final String query = "DELETE FROM users WHERE uid = ?";

        UserDSO toReturn = null;
        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement userDB = c.prepareStatement(query)) {

                removeLabelConnections(c, user.getID());
                removeEventConnections(c, user.getID());

                userDB.setString(1, user.getID());
                userDB.executeUpdate();

                toReturn = user;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException("The user: " + user.getName()
                        + " could not be deleted.", e.getMessage());
            }
        }
        return toReturn;
    }

    @Override
    public boolean isUnique(String userID) {
        final String query = "SELECT COUNT(*) AS numUsers FROM users WHERE uid = ?";
        boolean toReturn = false;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            toReturn = resultSet.getInt("numUsers") == 0;

        } catch (final SQLException e) {
            System.out.println("User ID: " + userID + " already exists.\n" + e.getMessage());
            e.printStackTrace();
            // will return false if unsuccessful
        }
        return toReturn;
    }

    @Override
    public int numUsers() {
        final String query = "SELECT COUNT(*) AS numUsers FROM users";
        int toReturn = -1;

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                toReturn = resultSet.getInt("numUsers");
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("The number of users could not be calculated.",
                    e.getMessage());
        }

        return toReturn;
    }

    /**
     * @param c Connection to the database.
     * @param labels List of Event Label objects associated with the User.
     * @param uid The unique (String) ID of the User.
     * @throws SQLException Any database / SQL issue.
     */
    private void addLabelConnections(Connection c, List<EventLabelDSO> labels, String uid) throws SQLException {
        final String query = "INSERT IGNORE INTO userslabels VALUES(?, ?)";

        try {
            for (EventLabelDSO label : labels) {
                final PreparedStatement statement = c.prepareStatement(query);
                statement.setString(1, uid);
                statement.setInt(2, label.getID());
                statement.executeUpdate();
                statement.close();
            }

        } catch (final SQLException e) {
            throw new SQLException("Could not connect labels to user: " + uid + ".", e);
        }
    }

    /**
     * @param c Connection to the database.
     * @param events List of Event objects associated with the User.
     * @param uid The unique (String) ID of the User.
     * @throws SQLException Any database / SQL issue.
     */
    private void addEventConnections(Connection c, List<EventDSO> events, String uid) throws SQLException {
        final String query = "INSERT IGNORE INTO usersevents VALUES(?, ?)";

        try {
            for (EventDSO event : events) {
                final PreparedStatement statement = c.prepareStatement(query);
                statement.setString(1, uid);
                statement.setInt(2, event.getID());
                statement.executeUpdate();
                statement.close();
            }

        } catch (final SQLException e) {
            throw new SQLException("Could not connect events to user: " + uid + ".", e);
        }
    }

    /**
     * @param c Connection to the database.
     * @param uid The unique (String) ID of the User.
     * @throws SQLException Any database / SQL issue.
     */
    private void removeLabelConnections(Connection c, String uid) throws SQLException {
        final String query = "DELETE FROM userslabels WHERE uid = ?";

        try {
            final PreparedStatement userLabels = c.prepareStatement(query);
            userLabels.setString(1, uid);
            userLabels.executeUpdate();
            userLabels.close();

        } catch (final SQLException e) {
            throw new SQLException("User: " + uid + "'s labels could not be disconnected.", e);
        }
    }

    /**
     * @param c Connection to the database.
     * @param uid The unique (String) ID of the User.
     * @throws SQLException Any database / SQL issue.
     */
    private void removeEventConnections(Connection c, String uid) throws SQLException {
        final String query = "DELETE FROM usersevents WHERE uid = ?";

        try {
            final PreparedStatement userEvents = c.prepareStatement(query);
            userEvents.setString(1, uid);
            userEvents.executeUpdate();
            userEvents.close();

        } catch (final SQLException e) {
            throw new SQLException("User: " + uid + "'s events could not be disconnected.", e);
        }
    }

    /**
     * @param user The User object to add Events to.
     */
    private void connectUsersAndEvents(UserDSO user) {
        final String query = "SELECT eid FROM usersevents WHERE usersevents.uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int eventID = resultSet.getInt("eid");
                for (EventDSO event : eventPersistence.getEventList()) {
                    if (event.getID() == eventID) {
                        user.addEvent(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Events could not be added to user: "
                    + user.getName() + ".", e.getMessage());
        }
    }

    /**
     * @param user The User object to add Event Label's to.
     */
    private void connectUsersAndLabels(UserDSO user) {
        final String query = "SELECT eventslabels.lid "
                + "FROM usersevents INNER JOIN eventslabels ON usersevents.eid = eventslabels.eid "
                + "WHERE usersevents.uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt("lid");
                for (EventLabelDSO label : eventLabelPersistence.getEventLabelList()) {
                    if (label.getID() == labelID) {
                        user.addLabel(label);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Labels could not be added to user: "
                    + user.getID() + ".", e.getMessage());
        }
    }

    /**
     * Set the favorites list (Event objects) in the User object.
     *
     * @param user The User object to add favourites to.
     */
    private void connectUsersAndFavorites(UserDSO user) {
        final String query = "SELECT eid FROM usersevents WHERE usersevents.uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int eventID = resultSet.getInt("eid");
                for (EventDSO event : eventPersistence.getEventList()) {
                    if (event.getID() == eventID && event.isFavorite()) {
                        user.addFavorite(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Favorites could not be added to user: "
                    + user.getName() + ".", e.getMessage());
        }
    }

} //UserPersistenceHSQLDB