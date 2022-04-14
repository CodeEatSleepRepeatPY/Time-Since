package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IUserPersistence {

    /**
     * @return List of Users (unmodifiable), null if unsuccessful.
     */
    List<UserDSO> getUserList();

    UserDSO getUserByID(int userID) throws UserNotFoundException;

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
     * @param newName The new name of the user.
     * @return The User object that was modified, null otherwise.
     * @throws UserNotFoundException If the User is not found in the database.
     */
    UserDSO updateUserName(UserDSO user, String newName) throws UserNotFoundException;

    /**
     * @param user The User object to be updated in the database.
     * @param newEmail The new email of the user.
     * @return The User object that was modified, null otherwise.
     * @throws UserNotFoundException If the User is not found in the database.
     */
    UserDSO updateUserEmail(UserDSO user, String newEmail) throws UserNotFoundException;

    /**
     * @param user The User object to be updated in the database.
     * @param newPassword The new password for the user.
     * @return he User object that was modified, null otherwise.
     * @throws UserNotFoundException If the User is not found in the database.
     */
    UserDSO updateUserPassword(UserDSO user, String newPassword) throws UserNotFoundException;

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

    /**
     * Gets all the events for the user.
     *
     * @param user the user
     * @return the list of events
     */
    List<EventDSO> getAllEvents(UserDSO user);

    /**
     * Gets all labels for the user.
     *
     * @param user the user
     * @return the list of labels
     */
    List<EventLabelDSO> getAllLabels(UserDSO user);

    /**
     * Gets all event favorites for the user.
     *
     * @param user the user
     * @return the list of favorite events
     */
    List<EventDSO> getFavorites(UserDSO user);

    //----------------------------------------
    // setters
    //----------------------------------------

    /**
     * Sets status of the event for the user.
     *
     * @param user       the user
     * @param event      the event
     * @param isComplete mark it as complete (true) or incomplete (false)?
     * @return the updated user
     */
    UserDSO setEventStatus(UserDSO user, EventDSO event, boolean isComplete);

    /**
     * Add an event to the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO addUserEvent(UserDSO user, EventDSO event);

    /**
     * Remove an event from the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO removeUserEvent(UserDSO user, EventDSO event);

    /**
     * Add a label to the user.
     *
     * @param user  the user
     * @param label the label
     * @return the updated user
     */
    UserDSO addUserLabel(UserDSO user, EventLabelDSO label);

    /**
     * Remove label from the user.
     *
     * @param user  the user
     * @param label the label
     * @return the updated user
     */
    UserDSO removeUserLabel(UserDSO user, EventLabelDSO label);

    /**
     * Add a favorite event to the user.
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO addUserFavorite(UserDSO user, EventDSO event);

    /**
     * Remove a favorite event from the user
     *
     * @param user  the user
     * @param event the event
     * @return the updated user
     */
    UserDSO removeUserFavorite(UserDSO user, EventDSO event);

}
