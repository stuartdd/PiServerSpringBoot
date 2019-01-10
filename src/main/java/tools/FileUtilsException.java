package tools;

public class FileUtilsException extends RuntimeException {

    public FileUtilsException(String string) {
        super(string);
    }

    public FileUtilsException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

}
