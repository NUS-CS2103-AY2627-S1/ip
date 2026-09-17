package nova.command;

import java.time.LocalDate;

import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays scheduled tasks that occur on a particular date.
 */
public class FindDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that searches the task list for a date.
     *
     * @param date date to search.
     */
    public FindDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Finds and displays tasks occurring on this command's date.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display matching tasks.
     * @param storage unused because searching does not change persistent data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks.findTasksOn(date));
    }
}
