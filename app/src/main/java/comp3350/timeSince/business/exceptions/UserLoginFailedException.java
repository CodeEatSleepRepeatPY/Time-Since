package comp3350.timeSince.business.exceptions;

public class UserLoginFailedException extends RuntimeException {
    public UserLoginFailedException(String message) {
        super("Login Failed, please retry!:\n" + message);
    }
}
