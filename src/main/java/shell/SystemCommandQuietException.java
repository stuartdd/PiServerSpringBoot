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
public class SystemCommandQuietException extends ServerRestException {

    public SystemCommandQuietException(String message) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency");

    }

    public SystemCommandQuietException(String message, Throwable cause) {
        super(message, HttpStatus.FAILED_DEPENDENCY, "Failed Dependency", cause);
    }

    @Override
    public boolean fullLog() {
        return false;
    }

}
