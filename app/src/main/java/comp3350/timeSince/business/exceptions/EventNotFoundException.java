package comp3350.timeSince.business.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String error) {
        super("The event was not found:\n" + error);
    }
}
