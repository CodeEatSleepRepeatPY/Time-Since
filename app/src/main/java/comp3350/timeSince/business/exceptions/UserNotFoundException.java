package comp3350.timeSince.business.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String error) {
        super("The user was not found:\n" + error);
    }
}
