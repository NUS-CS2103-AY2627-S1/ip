package yawned.exception;

/**
 * Represents an error caused by an invalid Yawned command.
 */
public class YawnedException extends Exception {

    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message Explanation of the invalid command.
     */
    public YawnedException(String message) {
        super(message);
    }
}
