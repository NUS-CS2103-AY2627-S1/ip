package nabmak.parser;

/**
 * Represents an exception caused by an invalid Nabmak command.
 */
public class NabmakException extends Exception {

    /**
     * Creates NabmakException with specified error message.
     *
     * @param err Error message.
     */
    public NabmakException(String err) {
        super(err);
    }
}
