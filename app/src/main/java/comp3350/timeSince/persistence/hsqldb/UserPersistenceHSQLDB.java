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
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.InitialDatabaseState;

public class UserPersistenceHSQLDB implements IUserPersistence {

    private final String dbPath;
    private final IEventPersistence eventPersistence;
    private final IEventLabelPersistence eventLabelPersistence;
    private int nextID;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        eventPersistence = Services.getEventPersistence(true);
        eventLabelPersistence = Services.getEventLabelPersistence(true);
        nextID = InitialDatabaseState.NUM_USERS; // number of values in the database at creation
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

        final int id = rs.getInt("uid");
        final String email = rs.getString("email");
        final String userName = rs.getString("user_name");
        final Calendar dateRegistered = DateUtils.timestampToCal(rs.getTimestamp("date_registered"));
        final String passwordHash = rs.getString("password_hash");

        UserDSO newUser = new UserDSO(id, email, dateRegistered, passwordHash);
        newUser.setName(userName);

        newUser = connectUsersAndEvents(newUser);
        newUser = connectUsersAndFavorites(newUser);
        newUser = connectUsersAndLabels(newUser);

        return newUser;
    }

    @Override
    public boolean userExists(UserDSO user) {
        try {
            return getUserByID(user.getID()).equals(user);
        } catch (UserNotFoundException e) {
            return false;
        }
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
            System.out.println("The list of users could not be returned.");
            e.printStackTrace();
            // will return null if unsuccessful.
        }
        return toReturn;
    }

    @Override
    public UserDSO getUserByID(int userID) throws UserNotFoundException {
        final String query = "SELECT * FROM users WHERE uid = ?";
        UserDSO toReturn = null;
        final String exceptionMessage = "The user: " + userID + " could not be found.";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, userID);
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
    public UserDSO getUserByEmail(String email) throws UserNotFoundException {
        final String query = "SELECT * FROM users WHERE email = ?";
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
        final String query = "INSERT INTO users VALUES(?, ?, ?, ?, ?)";
        UserDSO toReturn = null;

        if (newUser != null) {
            final String exceptionMessage = "The user: " + newUser.getName()
                    + " could not be added.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                int id = newUser.getID();
                if (id != -1 && isUnique(newUser.getEmail())) {
                    statement.setInt(1, id);
                    statement.setString(2, newUser.getEmail());
                    statement.setString(3, newUser.getName());
                    statement.setTimestamp(4, DateUtils.calToTimestamp(newUser.getDateRegistered()));
                    statement.setString(5, newUser.getPasswordHash());
                    int result = statement.executeUpdate();

                    if (result > 0) {
                        toReturn = newUser;
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                throw new DuplicateUserException(exceptionMessage);
            }
            if (toReturn == null) {
                throw new DuplicateUserException(exceptionMessage);
            }
        }
        nextID++;
        return toReturn;
    }

    @Override
    public UserDSO updateUserName(UserDSO user, String newName) {
        final String query = "UPDATE users SET user_name = ? WHERE uid = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newName);
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.setName(newName);
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
    public UserDSO updateUserEmail(UserDSO user, String newEmail) {
        final String query = "UPDATE users SET email = ? WHERE uid = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, user.getEmail());
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0 && user.setNewEmail(user.getEmail(),newEmail)) {
                    toReturn = user;
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
            if (toReturn == null) {
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUserPassword(UserDSO user, String newPassword) {
        final String query = "UPDATE users SET password_hash = ? WHERE uid = ?";
        UserDSO toReturn = null;

        if (user != null) {
            final String exceptionMessage = "The user: " + user.getName() +
                    " could not be updated.";

            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setString(1, newPassword);
                statement.setInt(2, user.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.setNewPassword(user.getPasswordHash(), newPassword);
                    toReturn = user;
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                throw new UserNotFoundException(exceptionMessage);
            }
            if (toReturn == null) {
                throw new UserNotFoundException(exceptionMessage);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) throws UserNotFoundException {
        final String query = "DELETE FROM users WHERE uid = ?";
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
        final String query = "SELECT COUNT(*) AS numUsers FROM users WHERE email = ?";
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

    @Override
    public int getNextID() {
        return nextID + 1;
    }

    @Override
    public List<EventDSO> getAllEvents(UserDSO user) throws UserNotFoundException {
        final String query = "SELECT * FROM usersevents WHERE uid = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null && userExists(user)) { // may throw an exception
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(resultSet.getInt("eid"));
                        if (event != null) {
                            toReturn.add(event);
                        }
                    } catch (EventNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        } else {
            throw new UserNotFoundException("The user is not in the database");
        }
        return toReturn;
    }

    @Override
    public List<EventLabelDSO> getAllLabels(UserDSO user) throws UserNotFoundException {
        final String query = "SELECT lid FROM userslabels WHERE uid = ?";
        List<EventLabelDSO> toReturn = new ArrayList<>();

        if (user != null && userExists(user)) { // may throw an exception
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(
                                resultSet.getInt("lid"));
                        if (label != null) {
                            toReturn.add(label);
                        }
                    } catch (EventLabelNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        } else {
            throw new UserNotFoundException("The user is not in the database");
        }
        return toReturn;
    }

    @Override
    public List<EventDSO> getFavorites(UserDSO user) throws UserNotFoundException {
        final String query = "SELECT eid FROM usersevents WHERE uid = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null && userExists(user)) { // may throw an exception
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(resultSet.getInt("eid"));
                        if (event != null && event.isFavorite()) {
                            toReturn.add(event);
                        }
                    } catch (EventNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        } else {
            throw new UserNotFoundException("The user is not in the database");
        }
        return toReturn;
    }

    @Override
    public UserDSO addUserEvent(UserDSO user, EventDSO event) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (user != null && event != null) {
            if (!userExists(user)) {
                throw new UserNotFoundException("User is not in the database");
            }
            if (!eventPersistence.eventExists(event)) {
                event = eventPersistence.insertEvent(event);
            }
            user = addUserEventConnection(user, event);
            toReturn = user;
        }
        return toReturn;
    }

    private UserDSO addUserEventConnection(UserDSO user, EventDSO event) {
        final String query = "INSERT INTO usersevents VALUES(?, ?)";

        if (user != null && event != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                statement.setInt(2, event.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.addEvent(event);
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return user;
    }

    @Override
    public UserDSO removeUserEvent(UserDSO user, EventDSO event) throws UserNotFoundException {
        final String query = "DELETE FROM events WHERE eid = ?";

        if (user != null && event != null) {
            if (userExists(user)) { // may throw an exception
                try (final Connection c = connection();
                     final PreparedStatement statement = c.prepareStatement(query)) {

                    statement.setInt(1, event.getID());
                    int result = statement.executeUpdate();

                    if (result > 0) {
                        user.removeEvent(event);
                    }
                } catch (final SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                throw new UserNotFoundException("The user is not in the database");
            }
        }
        return user;
    }

    @Override
    public UserDSO addUserLabel(UserDSO user, EventLabelDSO label) throws UserNotFoundException, EventLabelNotFoundException {
        UserDSO toReturn = null;
        if (user != null && label != null) {
            if (!userExists(user)) {
                throw new UserNotFoundException("User is not in the database");
            }
            if (!eventLabelPersistence.labelExists(label)) {
                label = eventLabelPersistence.insertEventLabel(label);
            }
            toReturn = addUserLabelConnection(user, label);
        }
        return toReturn;
    }

    private UserDSO addUserLabelConnection(UserDSO user, EventLabelDSO label) {
        final String query = "INSERT INTO userslabels VALUES(?, ?)";

        if (user != null && label != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                statement.setInt(2, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.addLabel(label);
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return user;
    }

    @Override
    public UserDSO removeUserLabel(UserDSO user, EventLabelDSO label) throws UserNotFoundException {
        final String query = "DELETE FROM labels WHERE lid = ?";

        if (user != null && label != null && userExists(user)) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, label.getID());
                int result = statement.executeUpdate();

                if (result > 0) {
                    user.removeLabel(label);
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new UserNotFoundException("The user is not in the database");
        }
        return user;
    }

    @Override
    public UserDSO addUserFavorite(UserDSO user, EventDSO event) throws UserNotFoundException, EventNotFoundException {
        UserDSO toReturn = null;
        if (user != null && event != null) {
            if (!userExists(user)) {
                throw new UserNotFoundException("User is not in the database");
            }
            if (!eventPersistence.eventExists(event)) {
                event = eventPersistence.insertEvent(event);
            }
            user = addUserEvent(user, event);
            toReturn = addFavoriteConnection(user, event);
        }
        return toReturn;
    }

    private UserDSO addFavoriteConnection(UserDSO user, EventDSO event) {
        final String query = "UPDATE events SET is_favorite = TRUE WHERE eid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            int result = statement.executeUpdate();

            if (result > 0) {
                user.addFavorite(event);
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    @Override
    public UserDSO removeUserFavorite(UserDSO user, EventDSO event) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (user != null && event != null) {
            if (userExists(user)) {
                toReturn = removeFavoriteConnection(user, event);
            } else {
                throw new UserNotFoundException("The user is not in the database");
            }
        }
        return toReturn;
    }

    private UserDSO removeFavoriteConnection(UserDSO user, EventDSO event) {
        final String query = "UPDATE events SET is_favorite = FALSE WHERE eid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setInt(1, event.getID());
            int result = statement.executeUpdate();

            if (result > 0) {
                user.removeFavorite(event);
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    /**
     * @param user The User object to add Events to.
     * @return The updated user.
     */
    private UserDSO connectUsersAndEvents(UserDSO user) {
        if (user != null) {
            List<EventDSO> favorites = getAllEventsHelper(user);
            for (EventDSO favorite : favorites) {
                user.addEvent(favorite);
            }
        }
        return user;
    }

    private List<EventDSO> getAllEventsHelper(UserDSO user) {
        final String query = "SELECT eid FROM usersevents WHERE uid = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(resultSet.getInt("eid"));
                        if (event != null) {
                            toReturn.add(event);
                        }
                    } catch (EventNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        }
        return toReturn;
    }

    /**
     * @param user The User object to add Event Label's to.
     * @return The updated user.
     */
    private UserDSO connectUsersAndLabels(UserDSO user) {
        if (user != null) {
            List<EventLabelDSO> labels = getAllLabelsHelper(user);
            for (EventLabelDSO label : labels) {
                user.addLabel(label);
            }
        }
        return user;
    }

    private List<EventLabelDSO> getAllLabelsHelper(UserDSO user) {
        final String query = "SELECT lid FROM userslabels WHERE uid = ?";
        List<EventLabelDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventLabelDSO label = eventLabelPersistence.getEventLabelByID(
                                resultSet.getInt("lid"));
                        if (label != null) {
                            toReturn.add(label);
                        }
                    } catch (EventLabelNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                // will return empty arraylist if unsuccessful
            }
        }
        return toReturn;
    }

    /**
     * Set the favorites list (Event objects) in the User object.
     *
     * @param user The User object to add favourites to.
     * @return The updated user.
     */
    private UserDSO connectUsersAndFavorites(UserDSO user) {
        if (user != null) {
            List<EventDSO> favorites = getFavoritesHelper(user);
            for (EventDSO favorite : favorites) {
                user.addFavorite(favorite);
            }
        }
        return user;
    }

    private List<EventDSO> getFavoritesHelper(UserDSO user) {
        final String query = "SELECT eid FROM usersevents WHERE uid = ?";
        List<EventDSO> toReturn = new ArrayList<>();

        if (user != null) {
            try (final Connection c = connection();
                 final PreparedStatement statement = c.prepareStatement(query)) {

                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        EventDSO event = eventPersistence.getEventByID(resultSet.getInt("eid"));
                        if (event != null && event.isFavorite()) {
                            toReturn.add(event);
                        }
                    } catch (EventNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

            } catch (final SQLException e) {
                e.printStackTrace();
                // will return an empty array list if unsuccessful
            }
        }
        return toReturn;
    }

} //UserPersistenceHSQLDB
