package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserPersistence implements IUserPersistence {

    private final List<UserDSO> userList;

    public UserPersistence() {
        this.userList = new ArrayList<>();
        insertUser(new UserDSO("admin", "1234"));
        insertUser(new UserDSO("kristjaf@myumanitoba.ca", "hash1"));
    }

    @Override
    public List<UserDSO> getUserList() {
        return Collections.unmodifiableList(userList);
    }

    @Override
    public UserDSO getUserByID(String uID) {
        UserDSO toReturn = null;
        for (int i = 0; i < userList.size() && toReturn == null; i++) {
            if (userList.get(i).getID().equals(uID)) {
                toReturn = userList.get(i);
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) {
        UserDSO toReturn = null;
        int index = userList.indexOf(newUser);
        if (index < 0) {
            userList.add(newUser);
            toReturn = newUser;
        } // else: duplicate
        return toReturn;
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        UserDSO toReturn = null;
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.set(index, user);
            toReturn = user;
        }
        return toReturn;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        UserDSO toReturn = null;
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.remove(index);
            toReturn = user;
        } // else: user is not in list
        return toReturn;
    }

    @Override
    public int numUsers() {
        return userList.size();
    }
}
