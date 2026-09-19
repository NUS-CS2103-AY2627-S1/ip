package lizzy.command;

import java.time.LocalDate;

import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Displays dated tasks that occur on one requested date.
 */
public class ViewScheduleCommand extends Command {
    /**
     * The date to search for.
     */
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on {@code date}.
     *
     * @param date the date to search for
     */
    public ViewScheduleCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the dated tasks that occur on the requested date.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(tasks, date);
    }
}
