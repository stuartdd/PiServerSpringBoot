package exceptions;

import org.springframework.http.HttpStatus;

/**
 *
 * @author - Stuartdd
 */
public class ResourceFileNotFoundException extends ServerRestException {

    public ResourceFileNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "Not Found");
    }

    public ResourceFileNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, "Not Found", cause);
    }

    @Override
    public boolean fullLog() {
        return false;
    }

}
