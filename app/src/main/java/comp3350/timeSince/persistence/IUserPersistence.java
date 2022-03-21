package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.UserDSO;

public interface IUserPersistence {

    List<UserDSO> getUserList();

    UserDSO getUserByID(String userID);

    UserDSO insertUser(UserDSO newUser);

    UserDSO updateUser(UserDSO user);

    UserDSO deleteUser(UserDSO user);

    int numUsers();

}
