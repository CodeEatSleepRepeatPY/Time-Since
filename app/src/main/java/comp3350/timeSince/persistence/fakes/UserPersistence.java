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
    private static int nextID;

    public UserPersistence() {
        this.userList = new ArrayList<>();
        nextID = 2;
    }

    @Override
    public List<UserDSO> getUserList() {
        return Collections.unmodifiableList(userList);
    }

    @Override
    public UserDSO getUserByEmail(String uID) throws UserNotFoundException {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getEmail().equals(uID)) {
                return userList.get(i);
            }
        }
        throw new UserNotFoundException("The user: " + uID + " could not be found.");
    }

    @Override
    public UserDSO insertUser(UserDSO newUser) throws DuplicateUserException {
        int index = userList.indexOf(newUser);
        if (index < 0) {
            userList.add(newUser);
            nextID++;
            return newUser;
        } // else: duplicate
        throw new DuplicateUserException("The user: " + newUser.getName() + " could not be added.");
    }

    @Override
    public UserDSO updateUser(UserDSO user) throws UserNotFoundException {
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.set(index, user);
            return user;
        }
        throw new UserNotFoundException("The user: " + user.getName() + " could not be updated.");
    }

    @Override
    public UserDSO updateUserName(UserDSO user) {
        return null;
    }

    @Override
    public UserDSO updateUserEmail(UserDSO user) {
        return null;
    }

    @Override
    public UserDSO updateUserPassword(UserDSO user) {
        return null;
    }

    @Override
    public UserDSO deleteUser(UserDSO user) throws UserNotFoundException {
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
            if (userID.equals(userList.get(i).getEmail())) {
                toReturn = false;
            }
        }
        return toReturn;
    }

    @Override
    public int numUsers() {
        return userList.size();
    }

    @Override
    public int getNextID() {
        return nextID;
    }

}
