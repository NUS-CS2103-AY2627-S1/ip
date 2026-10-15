package jiji.exception;

/**
 * Exception thrown when an invalid, out-of-bounds, or omitted task number is supplied.
 */
public class JijiInvalidIndexException extends JijiException {

    /**
     * Constructs a JijiInvalidIndexException with the specified error message.
     *
     * @param message The detailed error message.
     */
    public JijiInvalidIndexException(String message) {
        super(message);
    }

    /**
     * Creates an exception for an invalid or out-of-bounds task index.
     *
     * @return A new JijiInvalidIndexException for invalid indices.
     */
    public static JijiInvalidIndexException forInvalidNumber() {
        return new JijiInvalidIndexException("OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.");
    }

    /**
     * Creates an exception when a command requires a task index but none was provided.
     *
     * @param action The operation being performed (e.g. "mark" or "unmark").
     * @return A new JijiInvalidIndexException for missing index argument.
     */
    public static JijiInvalidIndexException forMissingIndex(String action) {
        return new JijiInvalidIndexException("OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a task number to " + action + ".");
    }
}
