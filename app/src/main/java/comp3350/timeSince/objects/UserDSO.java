/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */
// TODO: clean up
package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.PasswordErrorException;

public class UserDSO {

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final int ID;
    private String email; // could be email, or unique name, not null
    private String name;
    private final Calendar DATE_REGISTERED;
    private String passwordHash; // not null
    private final List<EventDSO> USER_EVENTS;
    private final List<EventDSO> USER_FAVORITES;
    private final List<EventLabelDSO> USER_LABELS;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public UserDSO(int id, String email, Calendar date, String passwordHash) {
        this.ID = id;
        this.email = email;
        this.name = email; // defaults to the email
        this.DATE_REGISTERED = date;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.USER_LABELS = new ArrayList<>();
        this.USER_EVENTS = new ArrayList<>();
        this.USER_FAVORITES = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Calendar getDateRegistered() {
        return DATE_REGISTERED;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<EventDSO> getUserEvents() {
        return Collections.unmodifiableList(USER_EVENTS);
    }

    public List<EventDSO> getUserFavorites() {
        return Collections.unmodifiableList(USER_FAVORITES);
    }

    public List<EventLabelDSO> getUserLabels() {
        return Collections.unmodifiableList(USER_LABELS);
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public boolean setNewEmail(String oldEmail, String newEmail) {
        boolean success = false;
        if (oldEmail != null && oldEmail.equals(email) && newEmail != null) {
            email = newEmail;
            success = true;
        }
        return success;
    }

    public void setName(String name) {
        this.name = name;
    }

    // confirm the old password before changing to the new password
    public boolean setNewPassword(String oldPasswordHash, String newPasswordHash) {
        boolean success = false;

        if (oldPasswordHash.equals(this.passwordHash)) {
            this.passwordHash = newPasswordHash;
            System.out.println("user set new password, success");
            success = true;
        }

        return success;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public boolean validate() {
        return (email != null && email.length() > 0
                && passwordHash != null
                && DATE_REGISTERED != null);
    }

    public void addLabel(EventLabelDSO newLabel) {
        if (newLabel != null && !USER_LABELS.contains(newLabel)) {
            USER_LABELS.add(newLabel);
        }
    }

    public void removeLabel(EventLabelDSO label) {
        USER_LABELS.remove(label);
    }

    public void addEvent(EventDSO newEvent) {
        if (newEvent != null && !USER_EVENTS.contains(newEvent)) {
            USER_EVENTS.add(newEvent);
        }
    }

    public void removeEvent(EventDSO event) {
        USER_EVENTS.remove(event);
    }

    public void addFavorite(EventDSO newFav) {
        if (newFav != null && !USER_FAVORITES.contains(newFav)) {
            USER_EVENTS.add(newFav); // should also be in events
            USER_FAVORITES.add(newFav);
        }
    }

    public void removeFavorite(EventDSO event) {
        USER_FAVORITES.remove(event);
    }

    // when logging in, have entered the right password?
    public boolean matchesExistingPassword(String password) throws PasswordErrorException {
        if (!passwordHash.equals(password)) {
            throw new PasswordErrorException("The entered passwords do not match!");
        }
        return true;
    }

    // does the passed password meet the new password requirements?
    // When register the password, at least one of the character should be capital letter
    // Ensure the password isn't too short(less than 8)
    public static boolean meetsNewPasswordReq(String password) throws PasswordErrorException {
        boolean minLength = hasMinLength(password);
        boolean hasCapital = hasCapital(password);
        if (!minLength) {
            throw new PasswordErrorException("The length of your password should more than 8 characters.");
        }
        if (!hasCapital) {
            throw new PasswordErrorException("Your password should contains at least one capital letter!");
        }
        return true;
    }

    public String toString() {
        String toReturn = "";
        if (name != null && email != null) {
            toReturn = String.format("Name: %s, UserID: %s", name, email);
        }
        if (name == null && email != null) {
            toReturn = String.format("UserID: %s", email);
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;

        if (other instanceof UserDSO) {
            toReturn = email.equals(((UserDSO) other).getEmail());
        }

        return toReturn;
    }

    // helper for meetsNewPasswordReq
    private static boolean hasMinLength(String password) {
        final int MIN_LENGTH = 8;

        return password.length() >= MIN_LENGTH;
    }

    // helper for meetsNewPasswordReq
    private static boolean hasCapital(String password) {
        boolean hasCapital = false;
        char letter;

        // checking that the password has a capital letter
        for (int i = 0; i < password.length() && !hasCapital; i++) {
            letter = password.charAt(i);
            if (Character.isUpperCase(letter)) {
                hasCapital = true;
            }
        }

        return hasCapital;
    }

}
