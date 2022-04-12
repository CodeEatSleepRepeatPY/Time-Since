package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Calendar;
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
        setDefaults();
        nextID = userList.size(); // number of values in the database at creation
    }

    @Override
    public List<UserDSO> getUserList() {
        return Collections.unmodifiableList(userList);
    }

    @Override
    public UserDSO getUserByID(int userID) throws UserNotFoundException {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getID() == userID) {
                return userList.get(i);
            }
        }
        throw new UserNotFoundException("The user: " + userID + " could not be found.");
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
    public UserDSO updateUserName(UserDSO user, String newName) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (user != null && user.validate()) {
            user.setName(newName);
            toReturn = updateUser(user);
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUserEmail(UserDSO user, String newEmail) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (user != null && user.validate()) {
            if (user.setNewEmail(user.getEmail(), newEmail)) {
                toReturn = updateUser(user); // may throw an exception
            }
        }
        return toReturn;
    }

    @Override
    public UserDSO updateUserPassword(UserDSO user, String newPassword) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (user != null && user.validate()) {
            if (user.setNewPassword(user.getPasswordHash(), newPassword)) {
                toReturn = updateUser(user); // may throw an exception
            }
        }
        return toReturn;
    }

    private UserDSO updateUser(UserDSO user) throws UserNotFoundException {
        int index = userList.indexOf(user);
        if (index >= 0) {
            userList.set(index, user);
            return user;
        }
        throw new UserNotFoundException("The user: " + user.getName() + " could not be updated.");
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
        return nextID + 1;
    }

    private void setDefaults() {
        Calendar initialDate = Calendar.getInstance();
        initialDate.set(2022, 3, 25, 15, 0, 8);

        UserDSO user1 = new UserDSO(1, "admin", initialDate,
                "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5");
        UserDSO user2 = new UserDSO(2, "kristjaf@myumanitoba.ca", initialDate,
                "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5");

        user1.setName("Admin");
        user2.setName("Freyja");

        userList.add(user1);
        userList.add(user2);
    }

}
