package luna.command;

import luna.exception.LunaException;

/**
 * Represents a user command that Luna can execute.
 */
public abstract class Command {

    /**
     * Executes this command and returns its response.
     *
     * @return Response to display to the user.
     * @throws LunaException If the command cannot be executed.
     */
    public abstract String execute() throws LunaException;

    /**
     * Returns whether this command exits the application.
     *
     * @return {@code true} if Luna should exit.
     */
    public boolean isExit() {
        return false;
    }
}
