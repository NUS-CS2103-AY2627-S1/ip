package duchess.ui.exceptions;

import duchess.parse.CommandType;

/**
 * Represents an Exception due to the user command.
 */
public class DuchessException extends Exception {

    /**
     * Creates a {@code DuchessException} with the desired message.
     */
    private DuchessException(String message) {
        super("Ohoho! No thanks!\n" + message);
    }

    /** Creates an exception for an unrecognised command. */
    public static DuchessException declareUnrecognizedCommand(String command) {
        return new DuchessException(String.format(
                "I refuse to dignify \"%s\" as a proper instruction.\n"
                        + "You may choose from the commands I have made available: %s",
                command,
                CommandType.getUnderstoodCommands()));
    }
}
