package comp3350.timeSince.business.exceptions;

public class EventNameInvalidException extends RuntimeException{
    public EventNameInvalidException(String error){
        super("The event name is not valid\n"+error);
    }
}
