/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserDSO {

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private String id; // could be email, or randomly generated
    private String name;
    private final Date DATE_REGISTERED; // generated when creating new object
    private String passwordHash;
    private final List<EventDSO> userEvents;
    private final List<EventDSO> favoritesList; // favorite Events
    private final List<EventLabelDSO> userLabels;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public UserDSO(String id, Date date, String passwordHash) {
        this.id = id;
        this.name = id; // defaults to the id
        this.DATE_REGISTERED = date;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.userLabels = new ArrayList<>();
        this.userEvents = new ArrayList<>();
        this.favoritesList = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDateRegistered() {
        return DATE_REGISTERED;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<EventDSO> getUserEvents() {
        return Collections.unmodifiableList(userEvents);
    }

    public List<EventDSO> getFavoritesList() {
        return Collections.unmodifiableList(favoritesList);
    }

    public List<EventLabelDSO> getUserLabels() {
        return Collections.unmodifiableList(userLabels);
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String name) {
        this.name = name;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public void addLabel(EventLabelDSO newLabel) {
        if (newLabel != null && !userLabels.contains(newLabel)) {
            userLabels.add(newLabel);
        }
    }

    public void removeLabel(EventLabelDSO label) {
        userLabels.remove(label);
    }

    public void addEvent(EventDSO newEvent) {
        if (newEvent != null && !userEvents.contains(newEvent)) {
            userEvents.add(newEvent);
        }
    }

    public void removeEvent(EventDSO event) {
        userEvents.remove(event);
    }

    public void addFavorite(EventDSO newFav) {
        if (newFav != null && !favoritesList.contains(newFav)) {
            favoritesList.add(newFav);
        }
    }

    public void removeFavorite(EventDSO event) {
        favoritesList.remove(event);
    }

    public String toString() {
        return String.format("Name: %s, UserID: %s", name, id);
    }

    public boolean equals(UserDSO other) {
        return this.id.equals(other.getID());
    }

    // does the passed password meet the new password requirements?
    // When register the password, at least one of the character should be capital letter
    // Ensure the password isn't too short(less than 8)
    public static boolean meetsNewPasswordReq(String password)  {
        return hasMinLength(password) && hasCapital(password);
    }

    // helper for meetsNewPasswordReq
    private static boolean hasMinLength(String password){
        final int MIN_LENGTH = 8;

        return password.length() >= MIN_LENGTH;
    }

    // helper for meetsNewPasswordReq
    private static boolean hasCapital(String password){
        boolean hasCapital = false;
        char letter;

        // checking that the password has a capital letter
        for(int i = 0; i < password.length() && !hasCapital;i++){
            letter = password.charAt(i);
            if (Character.isUpperCase(letter)){
                hasCapital = true;
            }
        }

        return hasCapital;
    }

    // confirm the old password before changing to the new password
    public boolean setNewPassword(String oldPasswordHash, String newPasswordHash){
        boolean success = false;

        if (oldPasswordHash.equals(this.passwordHash)){
            this.passwordHash = newPasswordHash;
            success = true;
        }

        return success;
    }

    // when logging in, have entered the right password?
    public boolean matchesExistingPassword(String passwordHash){
        return passwordHash.equals(this.passwordHash);
    }
}
