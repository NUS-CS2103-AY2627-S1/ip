package nova.command;

import java.time.LocalDate;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays deadlines occurring on a specified date.
 */
public class ShowOnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that displays deadlines on the given date.
     *
     * @param date date whose deadlines should be displayed
     */
    public ShowOnDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the numbered deadlines that occur on this command's date.
     *
     * @param tasks task list to search
     * @param ui UI used to display the matching deadlines
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showDeadlinesOn(date);
        for (TaskList.NumberedTask task : tasks.getDeadlinesOn(date)) {
            ui.showNumberedTask(task.getTaskNumber(), task.getTask());
        }
    }
}
