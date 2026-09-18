package benjamin;

/**
 * Signals a problem the user should be told about, such as a command that
 * cannot be understood or a save file line that cannot be read.
 *
 * <p>The message is shown to the user as it is, so it should read as plain
 * English rather than as a technical description.
 */
public class BenjaminException extends Exception {
    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message the wording to show, without any prefix.
     */
    public BenjaminException(String message) {
        super(message);
    }
}
