package exceptions;

import org.springframework.http.HttpStatus;

/**
 *
 * @author - Stuartdd
 */
public class ResourceNotFoundException extends ServerRestException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "Not Found");
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, "Not Found", cause);
    }

    @Override
    public boolean fullLog() {
        return false;
    }

}
