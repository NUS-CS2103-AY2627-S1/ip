package alfred.command;

import alfred.storage.Storage;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Displays tasks in chronological order without changing the task list.
 */
public class SortCommand extends Command {
    private final boolean isAscending;

    /**
     * Creates a command that displays tasks in the requested chronological direction.
     *
     * @param isAscending whether the earliest dated tasks appear first
     */
    public SortCommand(boolean isAscending) {
        this.isAscending = isAscending;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSortedTasks(tasks.getTasksSortedChronologically(isAscending), isAscending);
    }
}
