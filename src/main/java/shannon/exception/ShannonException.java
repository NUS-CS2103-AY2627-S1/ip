package shannon.exception;

/**
 * Base class for every error Shannon reports back to the user.
 * <p>
 * Each subclass's message is the text shown to the user, so the command loop can handle every
 * error with a single {@code catch} and one {@code println}.
 */
// Checked rather than unchecked: these are expected mistakes a user makes at the prompt, so the
// compiler should force the command loop to deal with them.
public class ShannonException extends Exception {

    /**
     * Creates an exception whose message is shown to the user as-is.
     *
     * @param message the text to show
     */
    public ShannonException(String message) {
        super(message);
    }
}
