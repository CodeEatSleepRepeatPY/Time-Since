package comp3350.timeSince.business;

import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IUserPersistence;

public class UserManager {
    private List<UserDSO> userList;
    private final IUserPersistence databasePersistence;

    public UserManager() {
        databasePersistence = Services.getUserPersistence();
        accessUsers();
    }

    private void accessUsers() {
        userList = databasePersistence.getUserList();
    }

    //-----------------------------------------
    // User account Manager Registration
    //-----------------------------------------

    public boolean uniqueName(String userName) {
        boolean unique = true;

        accessUsers();

        for (int i = 0; i < userList.size() && unique; i++) {
            if (userName.equals(userList.get(i).getID())) {
                unique = false;
            }
        }
        return unique;
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
        boolean exist = false;
        boolean correct = false;
        int accountIndex = 0; //store the index for the existing account in the list

        accessUsers();

        for (int i = 0; i < userList.size() && !exist; i++) {
            if (typedUserName.equals(userList.get(i).getID())) {
                exist = true;
                accountIndex = i;
            }
        }

        //If the username is exist, then we can check the password
        if (exist) {
            if (typedPassword.equals(userList.get(accountIndex).getPasswordHash())) {
                correct = true;
            }
        }
        return exist && correct;
    }

    public boolean loginProcess(String userName, String password) {
        return accountCheck(userName, password);
    }
}
