package comp3350.timeSince.business.exceptions;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(String message, String error) {
        super(message + "\n" + error);
    }

}
