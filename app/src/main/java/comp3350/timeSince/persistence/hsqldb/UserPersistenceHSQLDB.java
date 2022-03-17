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
        return null;
    }

    @Override
    public UserDSO getUserByID(String uID) {
        return null;
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) {
        try (final Connection c = connection()) {
            final PreparedStatement st = c.prepareStatement("INSERT INTO users VALUES(?, ?)");
            st.setString(1, newUser.getID());

            st.executeUpdate();

            return newUser;
        } catch (final SQLException e) {
            System.out.println("hello");
            return null;
        }
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        return null;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        return null;
    }

    @Override
    public int numUsers() {
        return -1;
    }

}
