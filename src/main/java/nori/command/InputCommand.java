package nori.command;

/**
 * Provides common input storage for commands entered with trailing details.
 */
abstract class InputCommand extends Command {
    /** The trimmed text the user typed after the command keyword. */
    protected final String details;

    /**
     * Creates a command with its input details.
     *
     * @param details the text after the command keyword.
     */
    InputCommand(String details) {
        this.details = details;
    }
}
