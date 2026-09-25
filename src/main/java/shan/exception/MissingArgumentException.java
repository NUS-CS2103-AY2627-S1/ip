package shan.exception;

/**
 * Represents a command that is missing a required argument.
 */
public class MissingArgumentException extends ShanException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a missing-argument exception.
     *
     * @param message Explanation of the missing argument.
     */
    public MissingArgumentException(String message) {
        super(message);
    }
}
