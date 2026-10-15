package remy.command;

import remy.storage.Storage;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Sorts tasks chronologically and persists the resulting canonical order.
 */
public class SortCommand extends Command {
    /** Whether dated tasks should be arranged from earliest to latest. */
    private final boolean isAscending;

    /**
     * Creates a chronological sort command with the requested direction.
     *
     * @param isAscending true to place earlier tasks first, or false to place later tasks first
     */
    public SortCommand(boolean isAscending) {
        this.isAscending = isAscending;
    }

    /**
     * Perform sort command
     *
     * @param tasks task collection to operate on
     * @param ui user interface used to display results
     * @param storage persistence service used to save changes
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.sortByDate(isAscending);
        saveTasks(tasks, ui, storage);
        ui.showTaskList(tasks.getTasks());
    }
}
