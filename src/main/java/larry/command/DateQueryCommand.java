package larry.command;

import java.time.LocalDate;

import larry.storage.Storage;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Displays tasks occurring on a specified date.
 */
public class DateQueryCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that searches for tasks occurring on the specified date.
     *
     * @param date Date whose tasks should be displayed.
     */
    public DateQueryCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(date, tasks);
    }
}
