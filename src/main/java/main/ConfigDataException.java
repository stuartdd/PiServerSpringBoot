package main;

import tools.*;

/**
 *
  * @author - Stuartdd
 */
public class ConfigDataException extends RuntimeException {

    public ConfigDataException(String string) {
        super(string);
    }

    public ConfigDataException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
}
