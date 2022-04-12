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
import comp3350.timeSince.persistence.IUserConnectionsPersistence;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserManager {

    private final IUserPersistence userPersistence;
    private final IUserConnectionsPersistence userConnectionsPersistence;

    public UserManager(boolean forProduction) {
        userPersistence = Services.getUserPersistence(forProduction);
        userConnectionsPersistence = Services.getUserConnectionsPersistence();
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

        UserDSO user = userPersistence.getUserByEmail(typedUserName);
        if (user != null && hashPassword(typedPassword).equals(user.getPasswordHash())) {
            toReturn = true;
        }
        if (user == null) {
            System.out.println("[LOG] test");
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

    public UserDSO getUserByEmail(String userID) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (userID != null) {
            toReturn = userPersistence.getUserByEmail(userID);
        }
        return toReturn;
    }

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    public UserDSO insertUser(String userID, String password, String confirmPassword, String name)
            throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {

        UserDSO toReturn = null; // default is null if something goes wrong
        if (UserDSO.meetsNewPasswordReq(password) && password.equals(confirmPassword)) {
            String hashedPassword = hashPassword(password);
            UserDSO newUser = new UserDSO(userPersistence.getNextID(), userID, Calendar.getInstance(), hashedPassword);
            if (newUser.validate()) {
                newUser.setName(name);
                toReturn = userPersistence.insertUser(newUser); // may cause exception
            }
        }
        return toReturn;
    }

    public UserDSO updateUserName(String userID, String newName) throws UserNotFoundException {
        UserDSO toReturn = null;

        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            toReturn = userPersistence.updateUserName(user, newName);
        }
        return toReturn;
    }

    // TODO: fix
    public UserDSO updateUserPassword(String userID, String newPassword)
            throws NoSuchAlgorithmException, UserNotFoundException {
        UserDSO toReturn = null;

        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            System.out.println("inside initial if statement");
            if (UserDSO.meetsNewPasswordReq(newPassword)) {
                System.out.println("inside second if statement");
                String newHash = hashPassword(newPassword);
                toReturn = userPersistence.updateUserPassword(user, newHash);
            }
        }
        return toReturn;
    }

    public UserDSO addUserEvent(String userID, EventDSO newEvent) throws UserNotFoundException {
        UserDSO toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID); // may cause exception
        if (user != null && user.validate() && newEvent != null && newEvent.validate()) {
            toReturn = userConnectionsPersistence.addUserEvent(user, newEvent);
        }
        return toReturn;
    }

    public UserDSO addUserFavorite(String userID, EventDSO fav) throws UserNotFoundException {
        UserDSO toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID); // may cause exception
        if (user != null && user.validate() && fav != null && fav.validate()) {
            toReturn = userConnectionsPersistence.addFavorite(user, fav);
        }
        return toReturn;
    }

    public UserDSO addUserLabel(String userID, EventLabelDSO label) throws UserNotFoundException {
        UserDSO toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID); // may cause exception
        if (user != null && user.validate() && label != null && label.validate()) {
            toReturn = userConnectionsPersistence.addUserLabel(user, label);
        }
        return toReturn;
    }

    public boolean deleteUser(String userID) throws UserNotFoundException {
        boolean toReturn = false; // default is false if something goes wrong
        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            if (userPersistence.deleteUser(user).equals(user)) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    public List<EventDSO> getUserEvents(String userID) throws UserNotFoundException {
        List<EventDSO> toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID); // may cause an exception
        if (user != null && user.validate()) {
            toReturn = userConnectionsPersistence.getAllEvents(user);
        }
        return toReturn;
    }

    public List<EventDSO> getUserFavorites(String userID) throws UserNotFoundException {
        List<EventDSO> toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID); // may cause an exception
        if (user != null && user.validate()) {
            toReturn = userConnectionsPersistence.getFavorites(user);
        }
        return toReturn;
    }

    public List<EventLabelDSO> getUserLabels(String userID) {
        List<EventLabelDSO> toReturn = null;
        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            toReturn = userConnectionsPersistence.getAllLabels(user);

        }
        return toReturn;
    }

}
