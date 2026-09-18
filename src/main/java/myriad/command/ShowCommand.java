package myriad.command;

import myriad.storage.Storage;
import myriad.task.TaskDateTime;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Shows every deadline or event occurring during a given date/time (see
 * {@link TaskList#getTasksOccurringOn} and
 * {@link myriad.task.Task#occursDuring}). Reads the list without changing
 * it, so it never saves.
 */
public class ShowCommand extends Command {

    private final TaskDateTime query;

    /**
     * Creates a command that reports the tasks occurring during
     * {@code query}.
     *
     * @param query the date, or date and time, to search for.
     */
    public ShowCommand(TaskDateTime query) {
        this.query = query;
    }

    /**
     * Shows the tasks occurring during the query date/time, or a "none
     * found" message if there are none.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(tasks.getTasksOccurringOn(query), query);
    }
}
