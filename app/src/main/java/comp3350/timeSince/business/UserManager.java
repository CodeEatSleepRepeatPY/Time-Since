package comp3350.timeSince.business;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
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

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    public UserDSO createUser(String userID, String password, String confirmPassword)
            throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {

        UserDSO toReturn = null; // default is null if something goes wrong

        if (validateEmail(userID) && validatePassword(password, confirmPassword)) {

            String hashedPassword = UserDSO.hashPassword(password);
            UserDSO newUser = new UserDSO(userPersistence.getNextID(), userID,
                    Calendar.getInstance(), hashedPassword);

            if (newUser.validate()) {
                toReturn = userPersistence.insertUser(newUser); // may cause exception
            }
        }
        return toReturn;
    }

    private boolean validateEmail(String userID) {
        boolean validEmail = UserDSO.emailVerification(userID);
        boolean uniqueEmail = userPersistence.isUnique(userID);
        return validEmail && uniqueEmail;
    }

    private boolean validatePassword(String password1, String password2) {
        boolean goodPassword = UserDSO.meetsNewPasswordReq(password1);
        boolean passwordMatch = password1.equals(password2);
        return goodPassword && passwordMatch;
    }

    //-------------------------------------------------------
    // User Account Manager Login
    //-------------------------------------------------------

    public boolean accountCheck(String typedUserName, String typedPassword)
            throws NoSuchAlgorithmException, UserNotFoundException {
        //first we need to check if this account is exist in the list
        boolean toReturn = false;

        UserDSO user = getUserByEmail(typedUserName); // may throw an exception
        if (user != null && UserDSO.hashPassword(typedPassword).equals(user.getPasswordHash())) {
            toReturn = true;
        }
        return toReturn;
    }

    public UserDSO getUserByEmail(String userID) throws UserNotFoundException {
        UserDSO toReturn = null;
        if (userID != null) {
            toReturn = userPersistence.getUserByEmail(userID);
        }
        return toReturn;
    }

    //-------------------------------------------------------
    // User Account Manager
    //-------------------------------------------------------

    public UserDSO updateUserName(String userID, String newName) throws UserNotFoundException {
        UserDSO toReturn = null;

        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            toReturn = userPersistence.updateUserName(user, newName);
        }
        return toReturn;
    }

    public UserDSO updateUserPassword(String userID, String newPassword)
            throws NoSuchAlgorithmException, UserNotFoundException {
        UserDSO toReturn = null;

        UserDSO user = userPersistence.getUserByEmail(userID);
        if (user != null && user.validate()) {
            if (UserDSO.meetsNewPasswordReq(newPassword)) {
                String newHash = UserDSO.hashPassword(newPassword);
                toReturn = userPersistence.updateUserPassword(user, newHash);
            }
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

}
