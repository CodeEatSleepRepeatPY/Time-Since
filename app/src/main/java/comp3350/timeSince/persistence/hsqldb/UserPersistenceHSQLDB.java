package comp3350.timeSince.persistence.hsqldb;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserPersistenceHSQLDB implements IUserPersistence {

    private final String dbPath;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private UserDSO fromResultSet(final ResultSet rs) throws SQLException {
        final String uID = rs.getString("userID");
        final UserDSO.MembershipType membershipType = UserDSO.MembershipType.valueOf(rs.getString("membership"));
        final String hash = rs.getString("password");
        return new UserDSO(uID, hash);
    }

    @Override
    public List<UserDSO> getUserList() {
        final List<UserDSO> users = new ArrayList<>();
        try (final Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                final UserDSO user = fromResultSet(resultSet);
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
    public UserDSO getUserByID(String uID) {
        try (final Connection c = connection()) {
            return null;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) {
        try (final Connection c = connection()) {
            return null;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        try (final Connection c = connection()) {
            return user;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        try (final Connection c = connection()) {
            return user;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public int numUsers() {
        int users = 0;
        try (final Connection c = connection()) {
            final Statement statement = c.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (resultSet.next()) {
                resultSet.last();
                users = resultSet.getRow();
            }
            resultSet.close();
            statement.close();
            return users;
        } catch (final SQLException e) {
            throw new PersistenceException(e);
        }
    }

}
