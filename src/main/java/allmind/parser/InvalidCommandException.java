package allmind.parser;

/** Indicates that a user command is not valid for the requested operation. */
public class InvalidCommandException extends Exception {
    /** Creates an exception with the supplied explanation. */
    public InvalidCommandException(String errorMessage) {
        super(errorMessage);
    }
}
