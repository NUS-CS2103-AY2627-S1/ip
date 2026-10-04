package penny.command;

/**
 * Represents a command that operates on a single task identified by its 0-based index.
 * Provides a shared index field for subclasses such as MarkCommand, UnmarkCommand,
 * and DeleteCommand, eliminating duplicate field and constructor declarations.
 */
public abstract class TaskIndexCommand extends Command {

    /** The 0-based index of the target task. */
    protected final int index;

    /**
     * Constructs a TaskIndexCommand with the specified task index.
     *
     * @param index The 0-based index of the target task.
     */
    protected TaskIndexCommand(int index) {
        assert index >= 0 : "Task index must be non-negative";
        this.index = index;
    }
}
