package comp3350.timeSince.business.exceptions;

public class UserRegistrationFailedException extends RuntimeException {

    public UserRegistrationFailedException(String message) {
        super("Registration Failed, please retry!:\n" + message);
    }

}
