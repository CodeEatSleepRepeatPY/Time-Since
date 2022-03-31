package comp3350.timeSince.business;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserManager {
    private final IUserPersistence databasePersistence;

    public UserManager(boolean forProduction) {
        databasePersistence = Services.getUserPersistence(forProduction);
    }

    //-----------------------------------------
    // User account Manager Registration
    //-----------------------------------------

    public boolean uniqueName(String userName) {
        return databasePersistence.isUnique(userName);
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

        UserDSO user = databasePersistence.getUserByID(typedUserName);
        if (user != null && hashPassword(typedPassword).equals(user.getPasswordHash())) {
            toReturn = true;
        }
        return toReturn;
    }

    public boolean loginProcess(String userName, String password)
            throws UserNotFoundException, NoSuchAlgorithmException {
        return accountCheck(userName, password);
    }

    //same method from database
    public UserDSO insertUser(UserDSO currentUser) throws DuplicateUserException {
        return databasePersistence.insertUser(currentUser);
    }

    public UserDSO updateUser(UserDSO currentUser) throws UserNotFoundException {
        return databasePersistence.updateUser(currentUser);
    }

    public void deleteUser(UserDSO currentUser) throws UserNotFoundException {
        databasePersistence.deleteUser(currentUser);
    }

    public String hashPassword(String inputPassword) throws NoSuchAlgorithmException {
        String strHash = "";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

        BigInteger notHash = new BigInteger(1, hash);
        strHash = notHash.toString(16);

        return strHash;
    }

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    public boolean tryRegistration(String newUsername, String newPassword, String confirmedPassword) {
        boolean success = false;
        if (uniqueName(newUsername) && passwordRequirements(newPassword)) {
            UserDSO newUser = new UserDSO(newUsername, Calendar.getInstance(), newPassword);
            if (newUser.matchesExistingPassword(confirmedPassword)) {
                success = true;
                //we should insert this new user into our database
                insertUser(newUser);
            }
        }
        return success;
    }
}
