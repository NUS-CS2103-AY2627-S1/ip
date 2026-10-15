package zeus.exception;

/** Represents an invalid command or operation reported by Zeus. */
public class ZeusException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation of the error.
     * @param message explanation shown to the user
     */
    public ZeusException(String message) {
        super(message);
    }
}
