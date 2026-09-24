package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Reports a command error identified during parsing.
 *
 * <p>This class is public so that {@code dobby.parser.Parser} can create it.</p>
 */
public final class InvalidCommand extends Command {
    /** Message explaining why the input was invalid. */
    private final String message;

    /** Creates a command that reports one error message. */
    public InvalidCommand(String message) {
        this.message = message;
    }

    /** Returns the parsing error for the UI to display. */
    @Override
    public String execute(DobbyLogic logic) {
        return message;
    }
}
