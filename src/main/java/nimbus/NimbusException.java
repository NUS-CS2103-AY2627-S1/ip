package nimbus;

/** Represents an error caused by an invalid user command. */
public class NimbusException extends Exception {
    /** Creates an exception with a user-facing explanation. */
    public NimbusException(String message) {
        super(message);
    }
}
