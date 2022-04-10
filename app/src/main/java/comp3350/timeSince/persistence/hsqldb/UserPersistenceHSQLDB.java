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
    private int nextID;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        this.eventPersistence = Services.getEventPersistence(true);
        this.eventLabelPersistence = Services.getEventLabelPersistence(true);
        nextID = 2;
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

        final int id = rs.getInt(TS.USER_ID);
        final String email = rs.getString(TS.EMAIL);
        final String userName = rs.getString(TS.USER_NAME);
        final Calendar dateRegistered = DateUtils.timestampToCal(rs.getTimestamp(TS.DATE_REGISTERED));
        final String passwordHash = rs.getString(TS.PASSWORD);

        UserDSO newUser = new UserDSO(id, email, dateRegistered, passwordHash);
        newUser.setName(userName);

        connectUsersAndEvents(newUser);
        connectUsersAndFavorites(newUser);
        connectUsersAndLabels(newUser);

        return newUser;
    }

    @Override
    public List<UserDSO> getUserList() {
        final String query = "SELECT * FROM " + TS.TABLE_USER;
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
            System.out.println("The list of users could not be returned.");
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
    }

    @Override
    public UserDSO getUserByEmail(String email) throws UserNotFoundException {
        final String query = "SELECT * FROM " + TS.TABLE_USER + " WHERE " + TS.EMAIL + " = ?";
        UserDSO toReturn = null;
        final String exceptionMessage = "The user: " + email + " could not be found.";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            throw new UserNotFoundException(exceptionMessage);
        }

        if (toReturn == null) {
            throw new UserNotFoundException(exceptionMessage);
        }
        return toReturn;
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) throws DuplicateUserException {
        final String query = "INSERT INTO " + TS.TABLE_USER + " VALUES(?, ?, ?, ?, ?)";
        UserDSO toReturn = null;

        if (newUser != null) {
            final String exceptionMessage = "The user: " + newUser.getName()
                    + " could not be added.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                int id = newUser.getID();
                if (isUnique(newUser.getEmail())) {
                    statement.setInt(1, id);
                    statement.setString(2, newUser.getEmail());
                    statement.setString(3, newUser.getName());
                    statement.setTimestamp(4, DateUtils.calToTimestamp(newUser.getDateRegistered()));
                    statement.setString(5, newUser.getPasswordHash());
                    int result = statement.executeUpdate();

                    if (result > 0) {
                        toReturn = newUser;
                    } else {
                        throw new DuplicateUserException(exceptionMessage);
                    }
                } else {
                    throw new DuplicateUserException(exceptionMessage);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                throw new DuplicateUserException(exceptionMessage);
            }

        }

        nextID++;
        return toReturn;
    }

    @Override
    public UserDSO updateUser(UserDSO user) throws UserNotFoundException {
        final String query = "UPDATE " + TS.TABLE_USER + " SET " + TS.EMAIL + " = ?, " + TS.USER_NAME + " = ?, "
                + TS.PASSWORD + " = ? WHERE " + TS.USER_ID + " = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getEmail());
                statement.setString(2, user.getName());
                statement.setString(3, user.getPasswordHash());
                statement.setInt(4, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = user;
                } else {
                    throw new UserNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUserName(UserDSO user) {
        final String query = "UPDATE " + TS.TABLE_USER
                + " SET " + TS.USER_NAME + " = ? WHERE " + TS.USER_ID + " = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getName());
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = user;
                } else {
                    throw new UserNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
        }

        return toReturn;
    }

    @Override
    public UserDSO updateUserEmail(UserDSO user) {
        final String query = "UPDATE " + TS.TABLE_USER
                + " SET " + TS.EMAIL + " = ? WHERE " + TS.USER_ID + " = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getEmail());
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = user;
                } else {
                    throw new UserNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUserPassword(UserDSO user) {
        final String query = "UPDATE " + TS.TABLE_USER
                + " SET " + TS.PASSWORD + " = ? WHERE " + TS.USER_ID + " = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getPasswordHash());
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = user;
                } else {
                    throw new UserNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) throws UserNotFoundException {
        final String query = "DELETE FROM " + TS.TABLE_USER + " WHERE " + TS.USER_ID + " = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName()
                    + " could not be deleted.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    toReturn = user;
                } else {
                    throw new UserNotFoundException(exceptionMessage);
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public boolean isUnique(String email) {
        final String query = "SELECT COUNT(*) AS numUsers FROM " + TS.TABLE_USER + " WHERE " + TS.EMAIL + " = ?";
        boolean toReturn = false;

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = resultSet.getInt("numUsers") == 0;
            }

        } catch (final SQLException e) {
            System.out.println("User ID: " + email + " already exists.");
            e.printStackTrace();
            // will return false if unsuccessful
        }
        return toReturn;
    }

    @Override
    public int numUsers() {
        final String query = "SELECT COUNT(*) AS numUsers FROM " + TS.TABLE_USER;
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

    public int getNextID() {
        return nextID + 1;
    }

    /**
     * @param user The User object to add Events to.
     */
    private void connectUsersAndEvents(UserDSO user) throws SQLException {
        final String query = "SELECT " + TS.EVENT_ID + " FROM usersevents WHERE " + TS.USER_ID + " = ?";

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int eventID = resultSet.getInt(TS.EVENT_ID);
                for (EventDSO event : eventPersistence.getEventList()) {
                    if (event.getID() == eventID) {
                        user.addEvent(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Events could not be added to user: "
                    + user.getName() + ".");
        }
    }

    /**
     * @param user The User object to add Event Label's to.
     */
    private void connectUsersAndLabels(UserDSO user) throws SQLException {
        final String query = "SELECT " + TS.LABEL_ID + " FROM userslabels WHERE " + TS.USER_ID + " = ?";
        List<EventLabelDSO> labels = eventLabelPersistence.getEventLabelList();

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int labelID = resultSet.getInt(TS.LABEL_ID);
                for (EventLabelDSO label : labels) {
                    if (label.getID() == labelID) {
                        user.addLabel(label);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Labels could not be added to user: "
                    + user.getEmail() + ".");
        }
    }

    /**
     * Set the favorites list (Event objects) in the User object.
     *
     * @param user The User object to add favourites to.
     */
    private void connectUsersAndFavorites(UserDSO user) throws SQLException {
        final String query = "SELECT " + TS.EVENT_ID + " FROM usersevents "
                + "WHERE " + TS.USER_ID + " = ? AND " + TS.FAVORITE + " = TRUE";
        List<EventDSO> events = eventPersistence.getEventList();

        try (Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, user.getID());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int eventID = resultSet.getInt(TS.EVENT_ID);

                for (EventDSO event : events) {
                    if (event.getID() == eventID) {
                        user.addFavorite(event);
                        break;
                    }
                }
            }

        } catch (final SQLException e) {
            throw new SQLException("Favorites could not be added to user: "
                    + user.getName() + ".");
        }
    }

} //UserPersistenceHSQLDB
