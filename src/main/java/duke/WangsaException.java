package duke;

/**
 * Reports an input error that Wangsa can explain to the user.
 */
public class WangsaException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with the supplied user-facing message.
     *
     * @param message User-facing explanation.
     */
    public WangsaException(String message) {
        super(message);
    }
}
