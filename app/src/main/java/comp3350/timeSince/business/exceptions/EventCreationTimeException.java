package comp3350.timeSince.business.exceptions;

public class EventCreationTimeException extends RuntimeException{
    public EventCreationTimeException(String error){
        super("The creation date for the event is before the current date!\n"+ error);
    }
}
