package jiji.command;

import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that lists tasks formatted for the user, with optional filtering by completion status.
 */
public class ListCommand extends Command {

    private final ListFilter filter;

    /**
     * Constructs a ListCommand with the specified filter mode.
     *
     * @param filter The filter mode to apply (ALL, PENDING, or DONE).
     */
    public ListCommand(ListFilter filter) {
        this.filter = (filter != null) ? filter : ListFilter.ALL;
    }

    /**
     * Constructs a new ListCommand that displays all tasks.
     */
    public ListCommand() {
        this(ListFilter.ALL);
    }

    /**
     * Returns the filter mode of this command.
     *
     * @return The {@link ListFilter}.
     */
    public ListFilter getFilter() {
        return filter;
    }

    /**
     * Executes the list command by displaying matching tasks via the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     * @return The formatted task list string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert ui != null : "Ui handler cannot be null";
        assert tasks != null : "TaskList cannot be null";

        switch (filter) {
            case PENDING:
                ui.showPendingTasks(tasks);
                return ui.formatPendingTasks(tasks);

            case DONE:
                ui.showDoneTasks(tasks);
                return ui.formatDoneTasks(tasks);

            case ALL:
            default:
                ui.showTaskList(tasks);
                return ui.formatTaskList(tasks);
        }
    }
}
