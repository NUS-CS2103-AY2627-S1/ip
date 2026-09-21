package echo.exception;

/**
 * A {@link EchoException} that signals that the delete command was entered without a task number or
 * with an unusable one.
 */
public class DeleteFormatException extends EchoException {
    /**
     * Creates the error shown when a delete command lacks a valid task number.
     * Shows the correct delete command format.*/
    public DeleteFormatException() {
        super("I need a mission number. Try: delete <taskNumber>");
    }
}
