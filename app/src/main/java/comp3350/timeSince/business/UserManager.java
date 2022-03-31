package comp3350.timeSince.business;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserManager {

    private final IUserPersistence databasePersistence;

    public UserManager() {
        databasePersistence = Services.getUserPersistence();
    }

    //-----------------------------------------
    // User account Manager Registration
    //-----------------------------------------

    public boolean uniqueName(String userName) {
        return databasePersistence.isUnique(userName);
    }

    //When register the password, at least one of the character should be capital letter
    //And the password and confirmed password should be same
    public boolean passwordRequirements(String password, String confirmedPassword) {
        int capital = 0; //count the number of capital letters in password
        final int MIN_LENGTH = 8;
        boolean capitalLetter =true;
        boolean match = true;
        boolean length = true;

        //checking each char in the password
        for(int i = 0; i < password.length();i++){
            char c = password.charAt(i);
            if(c >= 'A' && c <= 'Z') {
                capital++;
            }
        }

        if(capital<1){
            capitalLetter = false;
            throw new PasswordErrorException("Your password should contains at least one capital letter!");
        }

        if(!password.equals(confirmedPassword)){
            match = false;
            throw new PasswordErrorException("The entered passwords do not match!");
        }

        if (password.length() < 8){
            length = false;
            throw new PasswordErrorException("The length of your password should more than 8 characters.");
        }

        return capitalLetter&&match&&length;

    }

    //-------------------------------------------------------
    //User account Manager login
    //-------------------------------------------------------

    public boolean accountCheck(String typedUserName, String typedPassword) {
        //first we need to check if this account is exist in the list
        boolean toReturn = false;

        UserDSO user = databasePersistence.getUserByID(typedUserName);
        if (user != null && typedPassword.equals(user.getPasswordHash())) {
            toReturn = true;
        }
        return toReturn;
    }

    public boolean loginProcess(String userName, String password) {
        return accountCheck(userName, password);
    }

    //same method from database
    public UserDSO insertUser(UserDSO currentUser){
        return databasePersistence.insertUser(currentUser);
    }

    public UserDSO updateUser(UserDSO currentUser){
        return databasePersistence.updateUser(currentUser);
    }

    public void deleteUser(UserDSO currentUser){
        databasePersistence.deleteUser(currentUser);
    }

    public String hashPassword(String inputPassword) throws NoSuchAlgorithmException {
        //TODO test this method
        String strHash = "";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

        BigInteger notHash = new BigInteger(1,hash);
        strHash = notHash.toString(16);

        return strHash;
    }

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    public boolean tryRegistration(String newUsername, String newPassword,String confirmedPassword) {
        boolean success = false;
        if(uniqueName(newUsername) && passwordRequirements(newPassword,confirmedPassword)){
            success = true;
           // insertUser(new UserDSO(newUsername,new Date(System.currentTimeMillis()),newPassword));
        }
        return success;
    }
}
