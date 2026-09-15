package luke.exceptions;

/** Thrown when the first word of the input is not a supported command. */
public class InvalidCommandException extends UserInputException {
    /**
     * Creates an unknown-command error.
     *
     * @param message command keyword typed by the user
     */
    public InvalidCommandException(String message) {
        super("Unknown command: %s".formatted(message));
    }
}
