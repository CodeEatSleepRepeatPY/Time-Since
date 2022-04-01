package comp3350.timeSince.business;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserManager {

    private final IUserPersistence userPersistence;

    public UserManager(boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
    }

    //-----------------------------------------
    // User account Manager Registration
    //-----------------------------------------

    public boolean uniqueName(String userName) {
        return userPersistence.isUnique(userName);
    }

    public boolean passwordRequirements(String password) {
        return UserDSO.meetsNewPasswordReq(password);
    }

    //-------------------------------------------------------
    //User account Manager login
    //-------------------------------------------------------

    public boolean accountCheck(String typedUserName, String typedPassword)
            throws NoSuchAlgorithmException, UserNotFoundException {
        //first we need to check if this account is exist in the list
        boolean toReturn = false;

        UserDSO user = userPersistence.getUserByID(typedUserName);
        if (user != null && hashPassword(typedPassword).equals(user.getPasswordHash())) {
            toReturn = true;
        }
        return toReturn;
    }

    public String hashPassword(String inputPassword) throws NoSuchAlgorithmException {
        String strHash = "";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

        BigInteger notHash = new BigInteger(1, hash);
        strHash = notHash.toString(16);

        return strHash;
    }

    public UserDSO getUserByID(String userID) throws UserNotFoundException {
        UserDSO toReturn = null;

        if (userID != null) {
            toReturn = userPersistence.getUserByID(userID);
        }

        return toReturn;
    }

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    public boolean insertUser(String userID, String password, String confirmPassword, String name)
            throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {

        boolean toReturn = false; // default is false if something goes wrong
        if (password.equals(confirmPassword)) {
            String hashedPassword = hashPassword(password);
            UserDSO newUser = new UserDSO(userID, Calendar.getInstance(), hashedPassword);
            if (newUser.validate()) {
                newUser.setName(name);
                if (userPersistence.insertUser(newUser) != null) { // may cause exception
                    toReturn = true;
                }
            }
        }

        return toReturn;
    }

    public boolean updateUserName(String userID, String newName) throws UserNotFoundException {
        boolean toReturn = false;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            user.setName(newName);
            if (userPersistence.updateUser(user) != null) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    public boolean updateUserPassword(String userID, String oldPassword, String newPassword)
            throws NoSuchAlgorithmException, UserNotFoundException {
        boolean toReturn = false;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            if (UserDSO.meetsNewPasswordReq(newPassword)) {
                String oldHash = hashPassword(oldPassword);
                String newHash = hashPassword(newPassword);
                user.setNewPassword(oldHash,newHash);
                if (userPersistence.updateUser(user) != null) {
                    toReturn = true;
                }
            }
        }
        return toReturn;
    }

    public boolean addUserEvent(String userID, EventDSO newEvent) throws UserNotFoundException {
        boolean toReturn = false;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate() && newEvent.validate()) {
            user.addEvent(newEvent);
            if (userPersistence.updateUser(user) != null) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    public boolean addUserFavorite(String userID, EventDSO fav) throws UserNotFoundException {
        boolean toReturn = false;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate() && fav.validate()) {
            user.addFavorite(fav);
            if (userPersistence.updateUser(user) != null) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    public boolean addUserLabel(String userID, List<EventLabelDSO> labels) throws UserNotFoundException {
        boolean toReturn = false;

        if (labels != null) {
            UserDSO user = userPersistence.getUserByID(userID);
            for (EventLabelDSO label : labels) {
                if (user != null && user.validate() && label.validate()) {
                    user.addLabel(label);
                    if (userPersistence.updateUser(user) != null) {
                        toReturn = true;
                    }
                }
            }
        }
        return toReturn;
    }

    public boolean deleteUser(String userID) throws UserNotFoundException {
        boolean toReturn = false; // default is false if something goes wrong

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            if (userPersistence.deleteUser(user).equals(user)) {
                toReturn = true;
            }
        }

        return toReturn;
    }

    public List<EventDSO> getUserEvents(String userID) throws UserNotFoundException {
        List<EventDSO> toReturn = null;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            toReturn = user.getUserEvents();
        }

        return toReturn;
    }

    public List<EventDSO> getUserFavorites(String userID) {
        List<EventDSO> toReturn = null;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            toReturn = user.getFavoritesList();
        }

        return toReturn;
    }

    public List<EventLabelDSO> getUserLabels(String userID) {
        List<EventLabelDSO> toReturn = null;

        UserDSO user = userPersistence.getUserByID(userID);
        if (user != null && user.validate()) {
            toReturn = user.getUserLabels();
        }

        return toReturn;
    }

}
