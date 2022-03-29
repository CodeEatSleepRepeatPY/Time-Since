package comp3350.timeSince.business.exceptions;

public class EventLabelNotFoundException extends RuntimeException {
    public EventLabelNotFoundException(String error) {
        super("Event Label was not found:\n" + error);
    }
}
