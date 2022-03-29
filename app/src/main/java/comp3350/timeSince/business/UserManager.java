package comp3350.timeSince.business;


import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
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

    public boolean lengthCheck(String password) {
        final int MIN_LENGTH = 8;

        //this method is to ensure the password isn't too short(less than 8)
        return password.length() >= MIN_LENGTH;
    }

    //When register the password, at least one of the character should be capital letter
    //And the password and confirmed password should be same
    public boolean passwordRequirements(String password, String confirmedPassword) {
        int capital = 0; //count the number of capital letters in password

        //checking each char in the password
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                capital++;
            }
        }
        return capital >= 1 && password.equals(confirmedPassword);
    }

    //-------------------------------------------------------
    //User account Manager login
    //-------------------------------------------------------

    public boolean accountCheck(String typedUserName, String typedPassword) {
        //first we need to check if this account is exist in the list
        boolean toReturn = false;

        try {
            UserDSO user = databasePersistence.getUserByID(typedUserName);
            if (user != null && typedPassword.equals(user.getPasswordHash())) {
                toReturn = true;
            }
        } catch (UserNotFoundException e) {
            System.out.println("[LOG]: Account Check\n" + e.getMessage());
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
}
