package kong.command;

import java.time.LocalDate;

import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Shows deadlines and events that occur on a specific date.
 */
public class OnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on a date.
     *
     * @param date date to query
     */
    public OnDateCommand(LocalDate date) {
        this.date = date;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(tasks.getTasksOnDate(date));
    }
}
