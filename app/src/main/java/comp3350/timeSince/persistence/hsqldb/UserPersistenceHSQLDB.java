
package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PersistenceException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.business.DateUtils;

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

    private UserDSO fromResultSet(final ResultSet rs) throws SQLException {

        final String uID = rs.getString("uid");
        final String userName = rs.getString("user_name");
        final Date dateRegistered = DateUtils.timestampToDate(rs.getTimestamp("date_registered"));
        final String membershipType = rs.getString("membership_type");
        final String passwordHash = rs.getString("password_hash");

        UserDSO newUser = new UserDSO(uID, dateRegistered, passwordHash);
        newUser.setName(userName);
        newUser.setMembershipType(UserDSO.MembershipType.valueOf(membershipType));

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
            throw new PersistenceException("List of users could not be returned." + e.getMessage());
        }
    }

    @Override
    public UserDSO getUserByID(String userID) {
        final String query = "SELECT * FROM users WHERE uid = ?";
        UserDSO toReturn = null;
        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                toReturn = fromResultSet(resultSet);
            }
            return toReturn;

        } catch (final SQLException e) {
            throw new UserNotFoundException("The user: " + userID + " could not be found." + e.getMessage());
        }
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) {
        final String query = "INSERT INTO users VALUES(?, ?, ?, ?, ?)";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, newUser.getID());
            statement.setString(2, newUser.getName());
            statement.setTimestamp(3, DateUtils.dateToTimestamp(newUser.getDateRegistered()));
            statement.setString(4, newUser.getMembershipType().name());
            statement.setString(5, newUser.getPasswordHash());
            statement.executeUpdate();

            addLabelConnections(c, newUser.getUserLabels(), newUser.getID());
            addEventConnections(c, newUser.getUserEvents(), newUser.getID());

            return newUser;

        } catch (final SQLException e) {
            throw new DuplicateUserException("The user: " + newUser.getName() + " could not be added." + e.getMessage());
        }
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        final String query = "UPDATE users SET user_name = ?, membership_type = ?, "
                + "password_hash = ? WHERE uid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getMembershipType().name());
            statement.setString(3, user.getPasswordHash());
            statement.executeUpdate();

            addLabelConnections(c, user.getUserLabels(), user.getID());
            addEventConnections(c, user.getUserEvents(), user.getID());

            return user;

        } catch (final SQLException e) {
            throw new UserNotFoundException("The user: " + user.getName() + " could not be updated.");
        }
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        final String query = "DELETE FROM users WHERE uid = ?";

        try (final Connection c = connection();
             final PreparedStatement userDB = c.prepareStatement(query)) {

            removeLabelConnections(c, user.getID());
            removeEventConnections(c, user.getID());

            userDB.setString(1, user.getID());
            userDB.executeUpdate();

            return user;

        } catch (final SQLException e) {
            throw new UserNotFoundException("The user: " + user.getName() + " could not be deleted." + e.getMessage());
        }
    }

    @Override
    public boolean isUnique(String userID) {
        final String query = "SELECT count(*) AS numUsers FROM users WHERE uid = ?";

        try (final Connection c = connection();
             final PreparedStatement statement = c.prepareStatement(query)) {

            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("numUsers") == 0;

        } catch (final SQLException e) {
            throw new DuplicateUserException("User ID: " + userID + " already exists" + e.getMessage());
        }
    }

    @Override
    public int numUsers() {
        final String query = "SELECT count(*) AS numUsers FROM users";

        try (final Connection c = connection();
             final Statement statement = c.createStatement();
             final ResultSet resultSet = statement.executeQuery(query)) {

            resultSet.next();
            return resultSet.getInt("numUsers");

        } catch (final SQLException e) {
            throw new PersistenceException("The number of users could not be calculated." + e.getMessage());
        }
    }

    private void addLabelConnections(Connection c, List<EventLabelDSO> labels, String uid) throws SQLException {
        final String query = "INSERT IGNORE INTO userslabels VALUES(?, ?)";

        for (EventLabelDSO label : labels) {
            final PreparedStatement statement = c.prepareStatement(query);
            statement.setString(1, uid);
            statement.setInt(2, label.getID());
            statement.executeUpdate();
            statement.close();
        }
    }

    private void addEventConnections(Connection c, List<EventDSO> events, String uid) throws SQLException {
        final String query = "INSERT IGNORE INTO usersevents VALUES(?, ?)";

        for (EventDSO event : events) {
            final PreparedStatement statement = c.prepareStatement(query);
            statement.setString(1, uid);
            statement.setInt(2, event.getID());
            statement.executeUpdate();
            statement.close();
        }
    }

    private void removeLabelConnections(Connection c, String uid) throws SQLException {
        final String query = "DELETE FROM userslabels WHERE uid = ?";

        final PreparedStatement userLabels = c.prepareStatement(query);
        userLabels.setString(1, uid);
        userLabels.executeUpdate();
        userLabels.close();
    }

    private void removeEventConnections(Connection c, String uid) throws SQLException {
        final String query = "DELETE FROM usersevents WHERE uid = ?";

        final PreparedStatement userEvents = c.prepareStatement(query);
        userEvents.setString(1, uid);
        userEvents.executeUpdate();
        userEvents.close();
    }

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
            throw new PersistenceException("Events could not be added to the user " + user.getName() + e.getMessage());
        }
    }

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
            throw new PersistenceException("Labels could not be added to the user " + user.getID() + e.getMessage());
        }
    }

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
            throw new PersistenceException("Favorites could not be added to the user " + user.getName() + e.getMessage());
        }
    }

} //UserPersistenceHSQLDB