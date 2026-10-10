package cassava.data;

/**
 * Exception to be used when the data file is corrupted / cannot be parsed.
 */
public class FileFormatException extends Exception {

    public FileFormatException(String msg) {
        super(msg);
    }
}
