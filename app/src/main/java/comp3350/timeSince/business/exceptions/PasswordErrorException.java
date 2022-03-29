package comp3350.timeSince.business.exceptions;

public class PasswordErrorException extends RuntimeException{
    public PasswordErrorException(String message){
        super(message);
    }
}
