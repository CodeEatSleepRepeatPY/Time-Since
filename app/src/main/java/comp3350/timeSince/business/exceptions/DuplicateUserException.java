package comp3350.timeSince.business.exceptions;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String message, String error) {
        super(message + "\n" + error);
    }

}
