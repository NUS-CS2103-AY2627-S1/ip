package jonathan;

/** Represents an error caused by invalid user input or task data. */
public class JonathanException extends Exception {
    /** Creates an exception containing a user-facing validation message. */
    public JonathanException(String message) {
        super(message);
    }
}
