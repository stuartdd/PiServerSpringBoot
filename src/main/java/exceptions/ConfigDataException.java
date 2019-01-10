package exceptions;

import org.springframework.http.HttpStatus;

/**
 *
 * @author - Stuartdd
 */
public class ConfigDataException extends ServerRestException {

    public ConfigDataException(String message) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency");
    }

    public ConfigDataException(String message, Throwable cause) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency", cause);
    }

    @Override
    public boolean fullLog() {
        return false;
    }

}
