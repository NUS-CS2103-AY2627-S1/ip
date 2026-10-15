package allmind.parser;

/** Indicates that a valid command is missing a required field. */
public class MissingFieldException extends Exception {
    /** Creates an exception with the supplied explanation. */
    public MissingFieldException(String message) {
        super(message);
    }
}
