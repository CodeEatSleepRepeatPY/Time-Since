package comp3350.timeSince.business.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String error) {
        super("User with the same ID already exists:\n" + error);
    }
}
