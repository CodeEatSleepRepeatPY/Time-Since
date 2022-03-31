package comp3350.timeSince.business.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, String error) {
        super(message + "\n" + error);
    }

}
