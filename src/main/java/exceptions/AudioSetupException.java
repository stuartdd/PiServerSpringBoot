package exceptions;

import org.springframework.http.HttpStatus;

/**
 *
 * @author - Stuartdd
 */
public class AudioSetupException extends ServerRestException {

    public AudioSetupException(String message) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency");
    }

    public AudioSetupException(String message, Throwable cause) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency", cause);
    }

    @Override
    public boolean fullLog() {
        return true;
    }

}
