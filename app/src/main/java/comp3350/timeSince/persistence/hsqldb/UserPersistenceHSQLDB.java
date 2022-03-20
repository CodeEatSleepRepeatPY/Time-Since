package comp3350.timeSince.persistence.hsqldb;


import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;
import comp3350.timeSince.persistence.utils.DBHelper;

public class UserPersistenceHSQLDB implements IUserPersistence {

    private final String dbPath;
    private List<UserDSO> users;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
        loadUsers();
        System.out.println(users);
    }

    private Connection connection() throws SQLException {
        System.out.println("Start of connection");
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private UserDSO fromResultSet(final ResultSet rs) throws SQLException {
        final String uID = rs.getString("uid");
        final String userName = rs.getString("user_name");
        final String membershipType = rs.getString("membership_type");
        final String passwordHash = rs.getString("password_hash");
        UserDSO result = new UserDSO(uID, passwordHash);
        result.setName(userName);
        result.setMembershipType(UserDSO.MembershipType.valueOf(membershipType));
        System.out.println("RESULT: " + result.toString());
        return result;
    }

    private void loadUsers() {
        try (Connection connection = connection()) {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");

            while (resultSet.next()) {
                final UserDSO user = fromResultSet(resultSet);
                this.users.add(user);
            }
        } catch (final SQLException e) {
            System.out.println("Connect SQL: " + e.getMessage() + e.getSQLState());
            e.printStackTrace();
        }

    }

    public List<UserDSO> getUsers() {
        loadUsers();
        return users;
    }

    @Override
    public List<UserDSO> getUserList() {
        try (Connection c = connection()) {
            System.out.println("Testing1");
            Statement statement = c.createStatement();
            System.out.println("Testing2");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");
            System.out.println("Testing3");
            while (resultSet.next()) {
                System.out.println("Testing4");
                UserDSO user = fromResultSet(resultSet);
                System.out.println("Testing5");
                users.add(user);
                System.out.println("Testing6");
            }
            System.out.println("Testing7");
            resultSet.close();
            System.out.println("Testing8");
            statement.close();
            System.out.println("Testing9");
            return users;
        } catch (final SQLException e) {
            System.out.println("Testing10");
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
        return getUserList().size();

//        int users = 0;
//        try (final Connection c = connection()) {
//            final Statement statement = c.createStatement();
//            final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
//            if (resultSet.next()) {
//               resultSet.last();
//               users = resultSet.getRow();
//            }
//            resultSet.close();
//            statement.close();
//            return users;
//        } catch (final SQLException e) {
//            throw new PersistenceException(e);
//        }
    }

}
