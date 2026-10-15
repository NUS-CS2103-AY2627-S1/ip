package clearblue.command;

import java.time.LocalDate;
import java.util.List;

import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskDateTime;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Prints the deadlines and events on a given date, or reports that there
 * are none.
 */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    /**
     * Creates a command that searches for tasks on the given date.
     *
     * @param queryDate date to match against
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    /**
     * Prints the deadlines/events in {@code tasks} matching this command's
     * date, or reports that there are none. Reads only; does not modify or
     * save {@code tasks}.
     *
     * @param tasks task list to search
     * @param ui user interface to report through
     * @param storage unused
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getTasksOnDate(queryDate);
        String displayDate = TaskDateTime.formatDate(queryDate);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate(displayDate);
        } else {
            ui.showTasksOnDate(matches, displayDate);
        }
    }
}
