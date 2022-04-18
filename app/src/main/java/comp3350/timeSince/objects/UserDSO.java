package comp3350.timeSince.objects;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserRegistrationFailedException;

/**
 * UserDSO
 * <p>
 * Remarks: Domain Specific Object for a User
 */
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

    public UserDSO(int id, String email, Calendar dateRegistered, String passwordHash) {
        this.ID = id >= 1 ? id : -1;
        setEmailHelper(email);
        this.name = email; // defaults to the email
        this.DATE_REGISTERED = dateRegistered;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.USER_LABELS = new ArrayList<>();
        this.USER_EVENTS = new ArrayList<>();
        this.USER_FAVORITES = new ArrayList<>();
    }

    private void setEmailHelper(String email) {
        if (email != null) {
            if (emailVerification(email) || email.equals("admin")) {
                this.email = email;
            }
        } else {
            this.email = null;
        }
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

    public void setName(String name) {
        this.name = name;
    }

    public void addEvent(EventDSO newEvent) {
        if (newEvent != null && !USER_EVENTS.contains(newEvent)) {
            USER_EVENTS.add(newEvent);
            if (newEvent.isFavorite()) {
                addFavorite(newEvent);
            }
        }
    }

    public void removeEvent(EventDSO event) {
        USER_EVENTS.remove(event);
    }

    public void addFavorite(EventDSO newFav) {
        if (newFav != null && !USER_FAVORITES.contains(newFav)) {
            addEvent(newFav); // should also be in events
            USER_FAVORITES.add(newFav);
        }
    }

    public void removeFavorite(EventDSO event) {
        USER_FAVORITES.remove(event);
    }

    public void addLabel(EventLabelDSO newLabel) {
        if (newLabel != null && !USER_LABELS.contains(newLabel)) {
            USER_LABELS.add(newLabel);
        }
    }

    public void removeLabel(EventLabelDSO label) {
        USER_LABELS.remove(label);
    }

    //----------------------------------------
    // email
    //----------------------------------------

    public boolean setNewEmail(String oldEmail, String newEmail) throws UserRegistrationFailedException {
        boolean success = false;
        if (oldEmail != null && newEmail != null && oldEmail.equals(email)) {
            if (emailVerification(newEmail)) {
                email = newEmail;
                success = true;
            }
            else {
                throw new UserRegistrationFailedException("Not a valid email. " +
                        "Should be of the form local@domain (ex. username@domain.com)");
            }
        }
        return success;
    }

    /**
     * @param email the email address to verify
     * @return true if of the form: local@domain
     * @author taken from: <a href="https://www.baeldung.com/java-email-validation-regex">Baeldung</a>
     */
    public static boolean emailVerification(String email) {
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    //----------------------------------------
    // passwords
    //----------------------------------------

    public static String hashPassword(String inputPassword) throws NoSuchAlgorithmException {
        String strHash = "";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

        BigInteger notHash = new BigInteger(1, hash);
        strHash = notHash.toString(16);

        return strHash;
    }

    // confirm the old password before changing to the new password
    public boolean setNewPassword(String oldPasswordHash, String newPasswordHash) {
        boolean success = false;

        if (oldPasswordHash.equals(this.passwordHash) && meetsNewPasswordReq(newPasswordHash)) {
            this.passwordHash = newPasswordHash;
            System.out.println("user set new password, success");
            success = true;
        }

        return success;
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
        return (minLength && hasCapital);
    }

    // helper for meetsNewPasswordReq
    private static boolean hasMinLength(String password) throws PasswordErrorException {
        final int MIN_LENGTH = 8;
        if (password.length() >= MIN_LENGTH) {
            return true;
        } else {
            throw new PasswordErrorException("The length of your password should more than 8 characters.");
        }
    }

    // helper for meetsNewPasswordReq
    private static boolean hasCapital(String password) throws PasswordErrorException {
        char letter;
        // checking that the password has a capital letter
        for (int i = 0; i < password.length(); i++) {
            letter = password.charAt(i);
            if (Character.isUpperCase(letter)) {
                return true;
            }
        }
        throw new PasswordErrorException("Your password should contains at least one capital letter!");
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public boolean validate() {
        return (email != null
                && passwordHash != null
                && DATE_REGISTERED != null);
    }

    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;
        if (other instanceof UserDSO) {
            toReturn = ID == ((UserDSO) other).getID()
                    && email.equals(((UserDSO) other).getEmail());
        }
        return toReturn;
    }

    public String toString() {
        String toReturn = "";
        if (name != null && name.length() > 0 && email != null) {
            toReturn = String.format("Name: %s, Email: %s", name, email);
        }
        if ((name == null || name.length() == 0) && email != null) {
            toReturn = String.format("Email: %s", email);
        }
        return toReturn;
    }

}
