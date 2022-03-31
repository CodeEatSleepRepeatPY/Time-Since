package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserPersistence implements IUserPersistence {

    private final List<UserDSO> userList;

    public UserPersistence() {
        this.userList = new ArrayList<>();
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
        int index = userList.indexOf(newUser);
        if (index < 0) {
            userList.add(newUser);
            return newUser;
        } // else: duplicate
        throw new DuplicateUserException("The user: " + newUser.getName() + " could not be added.");
    }

    @Override
    public UserDSO updateUser(UserDSO user) {
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.set(index, user);
            return user;
        }
        throw new UserNotFoundException("The user: " + user.getName() + " could not be updated.");
    }

    @Override
    public UserDSO deleteUser(UserDSO user) {
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.remove(index);
            return user;
        } // else: user is not in list
        throw new UserNotFoundException("The user: " + user.getName() + " could not be deleted.");
    }

    @Override
    public boolean isUnique(String userID) {
        boolean toReturn = true;
        for (int i = 0; i < userList.size() && toReturn; i++) {
            if (userID.equals(userList.get(i).getID())) {
                toReturn = false;
            }
        }
        return toReturn;
    }

    @Override
    public int numUsers() {
        return userList.size();
    }

}
