package alfred.command;

import alfred.AlfredException;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Removes a live or archived task identified by a zero-based index.
 */
public class DeleteCommand extends Command {
    /** Zero-based index in the live list, or in the archive list when {@code isArchive} is set. */
    private final int index;

    /** {@code true} to delete from the archive view. */
    private final boolean isArchive;

    /**
     * Creates a command that will delete the live task at {@code index}.
     *
     * @param index Zero-based live-list index parsed from the user command.
     */
    public DeleteCommand(int index) {
        this(index, false);
    }

    /**
     * Creates a command that will delete a live or archived task.
     *
     * @param index Zero-based index in the chosen view.
     * @param isArchive {@code true} to delete from the archive list.
     */
    public DeleteCommand(int index, boolean isArchive) {
        this.index = index;
        this.isArchive = isArchive;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws AlfredException {
        Task deletedTask = isArchive ? tasks.deleteArchived(index) : tasks.delete(index);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
