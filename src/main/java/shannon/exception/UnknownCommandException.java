package shannon.exception;

/** Thrown when the first word of the input is not a command Shannon knows. */
public class UnknownCommandException extends ShannonException {

    private static final String COMMAND_LIST =
            "Try: todo, deadline, event, list, find, mark, unmark, delete or bye.";

    /**
     * Builds the message for an unrecognized command, listing the commands that do work.
     *
     * @param command the unrecognized command word, or an empty string if the user entered a blank line
     */
    public UnknownCommandException(String command) {
        super(command.isEmpty()
                ? "You didn't type anything! " + COMMAND_LIST
                : "Sorry, I don't know what \"" + command + "\" means. " + COMMAND_LIST);
    }
}
