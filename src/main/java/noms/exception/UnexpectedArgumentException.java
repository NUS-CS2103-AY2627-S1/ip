package noms.exception;

/** Indicates that a parameterless command received extra arguments. */
public class UnexpectedArgumentException extends NomsException {
    /**
     * Creates an exception explaining that a command takes no arguments.
     *
     * @param command the parameterless command word
     */
    public UnexpectedArgumentException(String command) {
        super("Noms doesn't need extra ingredients for the " + command + " command.\n"
                + "Try: " + command);
    }
}
