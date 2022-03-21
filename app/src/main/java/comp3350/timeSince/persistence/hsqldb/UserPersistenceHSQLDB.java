package comp3350.timeSince.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.utils.DateUtils;

public class UserPersistenceHSQLDB implements IUserPersistence {

    private final String dbPath;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private UserDSO fromResultSet(final ResultSet rs) throws SQLException {
        final String uID = rs.getString("uid");
        final String userName = rs.getString("user_name");
        final Date dateRegistered = DateUtils.timestampToDate(rs.getTimestamp("date_registered"));
        final String membershipType = rs.getString("membership_type");
        final String passwordHash = rs.getString("password_hash");
        UserDSO result = new UserDSO(uID, passwordHash);
        result.setName(userName);
        result.setMembershipType(UserDSO.MembershipType.valueOf(membershipType));
        return new UserDSO(uID, userName, dateRegistered, membershipType, passwordHash);
    }

    @Override
    public List<UserDSO> getUserList() {
        final List<UserDSO> users = new ArrayList<>();
        try (Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                UserDSO user = fromResultSet(resultSet);
                users.add(user);
            }
            resultSet.close();
            statement.close();
            return users;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO getUserByID(String userID) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("SELECT * FROM users WHERE uid = ?");
            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();
            UserDSO user = fromResultSet(resultSet);
            resultSet.close();
            statement.close();
            return user;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("INSERT INTO users VALUES(?, ?, ?, ?, ?)");
            statement.setString(1, newUser.getID());
            statement.setString(2, newUser.getName());
            statement.setTimestamp(3, DateUtils.dateToTimestamp(newUser.getDateRegistered()));
            statement.setString(4, newUser.getMembershipType());
            statement.setString(5, newUser.getPasswordHash());
            statement.executeUpdate();
            addLabelConnections(c, newUser.getUserLabels(), newUser.getID());
            addEventConnections(c, newUser.getUserEvents(), newUser.getID());
            statement.close();
            c.close();
            return newUser;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        try (final Connection c = connection()) {
            final PreparedStatement statement = c.prepareStatement("UPDATE users SET user_name = ?, membership_type = ?, password_hash = ? WHERE uid = ?");
            statement.setString(1, user.getName());
            statement.setString(2, user.getMembershipType());
            statement.setString(3, user.getPasswordHash());
            statement.executeUpdate();
            statement.close();
            addLabelConnections(c, user.getUserLabels(), user.getID());
            addEventConnections(c, user.getUserEvents(), user.getID());
            c.close();
            return user;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void addLabelConnections(Connection c, List<EventLabelDSO> labels, String uid) throws SQLException {
        Iterator<EventLabelDSO> iter = labels.iterator();
        while (iter.hasNext()) {
            EventLabelDSO label = iter.next();
            final PreparedStatement statement = c.prepareStatement("INSERT IGNORE INTO userslabels VALUES(?, ?)");
            statement.setString(1, uid);
            statement.setInt(2, label.getID());
            statement.executeUpdate();
            statement.close();
        }
    }

    private void addEventConnections(Connection c, List<EventDSO> events, String uid) throws SQLException {
        Iterator<EventDSO> iter = events.iterator();
        while (iter.hasNext()) {
            EventDSO event = iter.next();
            final PreparedStatement statement = c.prepareStatement("INSERT IGNORE INTO usersevents VALUES(?, ?)");
            statement.setString(1, uid);
            statement.setInt(2, event.getID());
            statement.executeUpdate();
            statement.close();
        }
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        try (final Connection c = connection()) {
            removeLabelConnections(c, user.getID());
            removeEventConnections(c, user.getID());
            final PreparedStatement userDB = c.prepareStatement("DELETE FROM users WHERE uid = ?");
            userDB.setString(1, user.getID());
            userDB.executeUpdate();
            return user;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private void removeLabelConnections(Connection c, String uid) throws SQLException {
        final PreparedStatement userLabels = c.prepareStatement("DELETE FROM userslabels WHERE uid = ?");
        userLabels.setString(1, uid);
        userLabels.executeUpdate();
    }

    private void removeEventConnections(Connection c, String uid) throws SQLException {
        final PreparedStatement userEvents = c.prepareStatement("DELETE FROM usersevents WHERE uid = ?");
        userEvents.setString(1, uid);
        userEvents.executeUpdate();
    }

    @Override
    public int numUsers() {
        return getUserList().size();
    }

}
