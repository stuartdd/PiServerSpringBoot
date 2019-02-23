package exceptions;

import org.springframework.http.HttpStatus;

public class BadDataException extends ServerRestException {

    public BadDataException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public BadDataException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", cause);
    }


}
