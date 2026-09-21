package peter.command;

import java.time.LocalDate;

import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Displays the scheduled tasks occurring on a given date.
 */
public class FindOnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that reports the tasks scheduled on a date.
     *
     * @param date date to report on.
     */
    public FindOnDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the task list without changing it, so nothing is saved and no
     * rollback is needed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOnDate(tasks, date);
    }
}
