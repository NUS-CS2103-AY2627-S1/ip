package samantha.command;

/**
 * Represents the command that requests undoing the most recent undoable command.
 */
public class UndoCommand extends Command {

    /**
     * Performs no task operation because the application owns command history.
     *
     * @param context current application state and persistence handlers, which are not needed here
     * @return an empty response because the application handles the undo
     */
    @Override
    public String execute(CommandContext context) {
        return "";
    }

    /**
     * Indicates that this command requests an undo operation.
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndo() {
        return true;
    }
}
