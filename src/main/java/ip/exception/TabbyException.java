
package ip.exception;

/** Represents a user-facing error raised by the application. */
public class TabbyException extends Exception {

    /** Creates an application exception with the supplied message. */
    public TabbyException(String message) {
        super(message);
    }
}
