// javadocs created by AI

package baron.exception;

/**
 * Signals that a command was invoked with an invalid argument format.
 */
public class FormatException extends BaronException {
    /**
     * Creates a format exception for a command that expected a specific syntax.
     *
     * @param commandName the command whose format was invalid.
     * @param expectedFormat the correct command syntax.
     */
    public FormatException(String commandName, String expectedFormat) {
        super("The command was malformed, traveler. Use this form: " + expectedFormat);
    }
}
