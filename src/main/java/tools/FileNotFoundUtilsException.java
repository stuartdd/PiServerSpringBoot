package tools;

public class FileNotFoundUtilsException extends FileUtilsException {

    public FileNotFoundUtilsException(String string) {
        super(string);
    }

    public FileNotFoundUtilsException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

}
