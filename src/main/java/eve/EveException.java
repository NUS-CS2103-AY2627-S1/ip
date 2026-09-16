package eve;

/** A chatbot-specific error, e.g. an invalid command or malformed arguments. */
public class EveException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message the message to show the user, e.g. "Oops, ...".
     */
    public EveException(String message) {
        super(message);
    }
}
