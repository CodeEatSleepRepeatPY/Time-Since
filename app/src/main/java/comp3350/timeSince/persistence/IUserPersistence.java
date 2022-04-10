package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.UserDSO;

public interface IUserPersistence {

    /**
     * @return List of Users (unmodifiable), null if unsuccessful.
     */
    List<UserDSO> getUserList();

    /**
     * @param userID The unique (String) ID of the Event.
     * @return The User object associated with the ID, null otherwise.
     * @throws UserNotFoundException If the User is not in the database.
     */
    UserDSO getUserByEmail(String userID) throws UserNotFoundException;

    /**
     * @param newUser The User object to be added to the database.
     * @return The User object that was added to the database, null otherwise.
     * @throws DuplicateUserException If the User is already stored in the database.
     */
    UserDSO insertUser(UserDSO newUser) throws DuplicateUserException;

    /**
     * @param user The User object to be updated in the database.
     * @return The User object that was modified, null otherwise.
     * @throws UserNotFoundException If the User is not found in the database.
     */
    UserDSO updateUser(UserDSO user) throws UserNotFoundException;

    /**
     * @param user The User object to be deleted from the database.
     * @return The User object that was deleted, null otherwise.
     * @throws UserNotFoundException If the User is not found in the database.
     */
    UserDSO deleteUser(UserDSO user) throws UserNotFoundException;

    /**
     * Check if there is already a User in the database with this ID.
     *
     * @param userID The possible identifier of the User.
     * @return true if there are no other users in the database with the same ID,
     *         false otherwise.
     */
    boolean isUnique(String userID);

    /**
     * @return The number of users in the database, -1 otherwise.
     */
    int numUsers();

    /**
     * @return The next unique ID if successful, -1 otherwise.
     */
    int getNextID();

}
