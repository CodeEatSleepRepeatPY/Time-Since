package comp3350.timeSince.business.exceptions;

public class DuplicateEventLabelException extends RuntimeException {

    public DuplicateEventLabelException(String message) {
        super(message);
    }

    public DuplicateEventLabelException(String message, String error) {
        super(message + "\n" + error);
    }

}
