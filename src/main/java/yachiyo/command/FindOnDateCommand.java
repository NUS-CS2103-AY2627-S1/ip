package yachiyo.command;

import java.time.LocalDate;
import java.util.List;

import yachiyo.storage.Storage;
import yachiyo.task.NumberedTask;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Displays deadlines and events that occur on a specified date.
 */
public class FindOnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that searches for tasks occurring on the supplied date.
     *
     * @param date date to search.
     */
    public FindOnDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays matching tasks while retaining their numbers from the complete list.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display the results.
     * @param storage unused because searching does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<NumberedTask> matchingTasks = tasks.getTasksOnDate(date);

        if (matchingTasks.isEmpty()) {
            ui.showNoTasksOnDate(date);
            return;
        }

        ui.showTasksOnDateHeader(date);
        ui.showNumberedTasks(matchingTasks);
    }
}
