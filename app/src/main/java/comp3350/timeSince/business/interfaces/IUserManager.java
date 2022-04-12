package comp3350.timeSince.business.interfaces;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IUserManager {
    boolean uniqueName(String userName);

    boolean passwordRequirements(String password);

    boolean accountCheck(String typedUserName, String typedPassword)
            throws NoSuchAlgorithmException, UserNotFoundException;

    String hashPassword(String inputPassword) throws NoSuchAlgorithmException;

    UserDSO getUserByEmail(String userID) throws UserNotFoundException;

    //This method is called when the register button is hit
    //to show if the user create a new account successfully or not
    UserDSO insertUser(String userID, String password, String confirmPassword, String name)
            throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException;

    UserDSO updateUserName(String userID, String newName) throws UserNotFoundException;

    // TODO: fix
    UserDSO updateUserPassword(String userID, String newPassword)
            throws NoSuchAlgorithmException, UserNotFoundException;

    UserDSO addUserEvent(String userID, EventDSO newEvent) throws UserNotFoundException;

    UserDSO addUserFavorite(String userID, EventDSO fav) throws UserNotFoundException;

    UserDSO addUserLabel(String userID, EventLabelDSO label) throws UserNotFoundException;

    boolean deleteUser(String userID) throws UserNotFoundException;

    List<EventDSO> getUserEvents(String userID) throws UserNotFoundException;

    List<EventDSO> getUserFavorites(String userID) throws UserNotFoundException;

    List<EventLabelDSO> getUserLabels(String userID);
}
