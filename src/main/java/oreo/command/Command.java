package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;

/** A user command that can be executed against the task application state. */
public abstract class Command {
    /** Shared parser used by commands that need argument validation. */
    protected final Parser parser;

    /** Creates a command using the supplied parser dependency. */
    protected Command(Parser parser) {
        this.parser = parser;
    }

    /**
     * Executes this command against the supplied application context.
     *
     * @param context the application state and collaborators used by the command
     * @throws OreoException when the command input is invalid
     */
    public abstract void execute(AppContext context) throws OreoException;

    /** Returns whether this command ends the application. */
    public boolean isExit() {
        return false;
    }
}
