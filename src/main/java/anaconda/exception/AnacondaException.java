package anaconda.exception;

/**
 * Represents invalid input or a failure to complete an Anaconda command.
 */
public class AnacondaException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Distinguishes correctable command input from unknown commands and storage failures.
     */
    public enum Reason {
        INVALID_INPUT, UNKNOWN_COMMAND, STORAGE_ERROR
    }

    private final Reason reason;

    /**
     * Creates an exception containing a user-facing explanation.
     *
     * @param message Explanation of the invalid input.
     */
    public AnacondaException(String message) {
        this(message, Reason.INVALID_INPUT);
    }

    /**
     * Creates an exception with a category independent of its user-facing wording.
     *
     * @param message Explanation of the failure.
     * @param reason Category used to present the failure.
     */
    public AnacondaException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
