package pulbot;

/** Represents an expected error while processing a Pulbot command or file. */
public class PulbotException extends Exception {
    /** Creates an exception with the supplied user-facing message. */
    public PulbotException(String message) {
        super(message);
    }
}
