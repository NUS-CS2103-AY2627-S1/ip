package discitrack.command;

import java.time.LocalDate;

import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Shows tasks that are relevant to a specific date.
 */
public class CheckDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a checkdate command for the given date.
     *
     * @param date the date to check tasks against.
     */
    public CheckDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the tasks that match this command's date.
     *
     * @param tasks the task list to search.
     * @param ui the UI used to show matching tasks.
     * @param storage the storage used by the application.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showDateMatches(tasks.findTasksByDate(date), tasks.asList());
    }
}
