package daddy.command;

import java.time.LocalDate;

import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Displays the deadline and event tasks that occur on a specific date.
 */
public class ListOnDateCommand extends Command {
    /** Stores the date used to filter listed tasks. */
    private final LocalDate date;

    /**
     * Creates a command that filters tasks by date.
     *
     * @param date the date to match
     */
    public ListOnDateCommand(LocalDate date) {
        this.date = date;
    }

    /** Displays the tasks that occur on this command's date. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(date, tasks.getTasksOccurringOn(date));
    }
}
