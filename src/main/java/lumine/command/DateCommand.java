package lumine.command;

import java.time.LocalDate;

import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Lists pending tasks due on a given date.
 */
public class DateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that will filter tasks due on the given date.
     *
     * @param date the calendar date to filter by.
     */
    public DateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     * Displays pending deadlines and events whose end date matches the stored date.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        ui.showMessage(taskList.formatTasksDueOn(date));
    }
}
