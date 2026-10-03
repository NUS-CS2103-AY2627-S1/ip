package verity.command;

import java.io.IOException;

import verity.exception.VerityException;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    private final boolean isExit;

    /**
     * Creates a command.
     *
     * @param isExit Whether this command exits the chatbot.
     */
    protected Command(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Executes the command and returns its user-facing response.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     * @throws IOException If changes cannot be saved.
     * @throws VerityException If the command cannot be completed.
     */
    public abstract String execute(CommandContext context)
            throws IOException, VerityException;

    /**
     * Returns whether this command exits the chatbot.
     *
     * @return True if this is an exit command.
     */
    public boolean isExit() {
        return isExit;
    }
}
