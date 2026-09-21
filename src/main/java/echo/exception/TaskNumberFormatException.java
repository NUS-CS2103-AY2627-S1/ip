package echo.exception;

/**
 * An {@link EchoException} when the input format does not match what is expected of mark and unmark.
 */
public class TaskNumberFormatException extends EchoException {
    /**
     * Creates the error shown when a mark/unmark command lacks a task number.
     * @param keyword command keyword, e.g. {@code mark}
     */
    public TaskNumberFormatException(String keyword) {
        super("I need a mission number. Try: " + keyword + " <taskNumber>");
    }
}
