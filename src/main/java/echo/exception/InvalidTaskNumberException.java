package echo.exception;

/**
 * An {@link EchoException} created when the input task number does not correspond to an existing task.
 */
public class InvalidTaskNumberException extends EchoException {
    /** Creates the error shown when the given task number matches no task. */
    public InvalidTaskNumberException() {
        super("That mission number is not on the manifest. Use list to check the available numbers.");
    }
}
