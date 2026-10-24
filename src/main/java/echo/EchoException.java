package echo;

/**
 * Checked exception for user-facing errors specific to Echo. The message is
 * shown to the user (prefixed by the loop's catch block), so it should read
 * as a clear explanation of what went wrong.
 */
public class EchoException extends Exception {
    /** Creates an EchoException with the given user-facing message. */
    public EchoException(String message) {
        super(message);
    }
}
