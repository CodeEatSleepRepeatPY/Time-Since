package comp3350.timeSince.business.exceptions;

public class EventLabelNotFoundException extends RuntimeException {

    public EventLabelNotFoundException(String message) {
        super(message);
    }

    public EventLabelNotFoundException(String message, String error) {
        super(message + "\n" + error);
    }

}
