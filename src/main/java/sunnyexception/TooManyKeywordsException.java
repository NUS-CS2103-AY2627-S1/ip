package sunnyexception;

/**
 * Indicates that the user entered more than one keyword for the find command.
 */
public class TooManyKeywordsException extends SunnyException {

    /**
     * Constructor for the exception that prints a message.
     */
    public TooManyKeywordsException(String message) {
        super(message);
    }
}
