package shan.exception;

/**
 * Represents a command argument that has an invalid value.
 */
public class InvalidArgumentException extends ShanException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an invalid-argument exception.
     *
     * @param message Explanation of the invalid argument.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
