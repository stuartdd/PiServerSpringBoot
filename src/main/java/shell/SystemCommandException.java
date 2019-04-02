/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shell;

import exceptions.ServerRestException;
import org.springframework.http.HttpStatus;

/**
 *
 * @author stuar
 */
public class SystemCommandException extends ServerRestException {

    public SystemCommandException(String message) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency");

    }

    public SystemCommandException(String message, Throwable cause) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency", cause);
    }

    @Override
    public boolean fullLog() {
        return true;
    }

}
