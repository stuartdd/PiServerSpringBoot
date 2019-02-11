package exceptions;

import tools.*;

public class ServiceException extends RuntimeException {

    public ServiceException(String string) {
        super(string);
    }

    public ServiceException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

}
