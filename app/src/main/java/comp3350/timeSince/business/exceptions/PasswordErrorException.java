package comp3350.timeSince.business.exceptions;

public class PasswordErrorException extends RuntimeException {

    public PasswordErrorException(String message) {
        super(" is not valid. Your password should have at least one capital letter & length >= 8 ");
    }

}

