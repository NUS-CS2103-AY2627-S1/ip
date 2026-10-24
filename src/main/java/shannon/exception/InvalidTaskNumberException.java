package shannon.exception;

/**
 * Thrown when a command that expects a task number is given something that is not a whole
 * number, e.g. {@code mark two}, or nothing at all.
 */
public class InvalidTaskNumberException extends ShannonException {

    /**
     * Builds the message for a task number that could not be read.
     *
     * @param command  the command word, so the example matches what the user typed
     * @param argument the text that could not be read as a task number
     */
    public InvalidTaskNumberException(String command, String argument) {
        super(argument.isEmpty()
                ? "Which task? Give me a task number, for example: " + command + " 2"
                : "\"" + argument + "\" is not a task number. Try: " + command + " 2");
    }
}
