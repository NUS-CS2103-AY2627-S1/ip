package shannon.exception;

/**
 * Thrown when a task command is given without a description,
 * e.g. {@code todo} on its own, or {@code deadline /by Friday}.
 */
public class EmptyDescriptionException extends ShannonException {

    /**
     * Builds the message for a command that was missing its description.
     *
     * @param command the command word that was missing a description
     * @param example a full, valid example of that command to show the user
     */
    public EmptyDescriptionException(String command, String example) {
        super("The description of a " + command + " cannot be empty, for example: " + example);
    }
}
