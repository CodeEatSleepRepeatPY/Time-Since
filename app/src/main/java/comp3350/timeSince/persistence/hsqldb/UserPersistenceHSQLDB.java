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
        List<UserDSO> toReturn = null;
        final List<UserDSO> users = new ArrayList<>();

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                UserDSO user = fromResultSet(resultSet);
                users.add(user);
            }
            toReturn = users;

        } catch (final SQLException e) {
            System.out.println("The list of users could not be returned.\n" + e.getMessage());
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
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
                    + " could not be found.\n" + e.getMessage());
        }
        return toReturn;
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) throws DuplicateUserException {
        final String query = "INSERT INTO users VALUES(?, ?, ?, ?)";

        UserDSO toReturn = null;
        if (newUser != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newUser.getID());
                statement.setString(2, newUser.getName());
                statement.setTimestamp(3, DateUtils.calToTimestamp(newUser.getDateRegistered()));
                statement.setString(4, newUser.getPasswordHash());
                statement.executeUpdate();

                toReturn = newUser;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new DuplicateUserException("The user: " + newUser.getName()
                        + " could not be added.\n" + e.getMessage());
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
                statement.setString(3, user.getID());
                statement.executeUpdate();

                toReturn = user;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException("The user: " + user.getName() +
                        " could not be updated.\n" + e.getMessage());
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

                userDB.setString(1, user.getID());
                userDB.executeUpdate();

                toReturn = user;

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException("The user: " + user.getName()
                        + " could not be deleted.\n" + e.getMessage());
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
            System.out.println("The number of users could not be calculated.\n" + e.getMessage());
            e.printStackTrace();
            // will return -1 if unsuccessful.
        }

        return toReturn;
    }

    /**
     * @param user The User object to add Events to.
     */
    private void connectUsersAndEvents(UserDSO user) throws SQLException {
        final String query = "SELECT event_name FROM events WHERE uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String eventID = resultSet.getString("event_name");
                for (EventDSO event : eventPersistence.getEventList()) {
                    if (event.getName().equals(eventID)) {
                        user.addEvent(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Events could not be added to user: "
                    + user.getName() + ".", e.getMessage());
        }
    }

    /**
     * @param user The User object to add Event Label's to.
     */
    private void connectUsersAndLabels(UserDSO user) throws SQLException {
        final String query = "SELECT label_name FROM labels WHERE uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String labelName = resultSet.getString("label_name");
                for (EventLabelDSO label : eventLabelPersistence.getEventLabelList()) {
                    if (label.getName().equals(labelName)) {
                        user.addLabel(label);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Labels could not be added to user: "
                    + user.getID() + ".", e.getMessage());
        }
    }

    /**
     * Set the favorites list (Event objects) in the User object.
     *
     * @param user The User object to add favourites to.
     */
    private void connectUsersAndFavorites(UserDSO user) throws SQLException {
        final String query = "SELECT event_name FROM events WHERE uid = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String eventName = resultSet.getString("event_name");
                for (EventDSO event : eventPersistence.getEventList()) {
                    if (event.getName().equals(eventName) && event.isFavorite()) {
                        user.addFavorite(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Favorites could not be added to user: "
                    + user.getName() + ".", e.getMessage());
        }
    }

} //UserPersistenceHSQLDB
