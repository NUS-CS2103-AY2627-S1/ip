package penny.command;

import java.time.LocalDate;
import java.util.List;

import penny.common.Time;
import penny.storage.Storage;
import penny.task.Task;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents a command to find and list all tasks occurring on a specific date.
 */
public class OnCommand extends Command {

    private final LocalDate targetDate;

    /**
     * Constructs an OnCommand with the target date to query.
     *
     * @param targetDate The date to search for tasks.
     */
    public OnCommand(LocalDate targetDate) {
        assert targetDate != null : "Target date must not be null";
        this.targetDate = targetDate;
    }

    /**
     * Executes the date query command, filtering tasks occurring on the target date
     * and displaying them via the user interface.
     *
     * @param tasks The task list searched for matching tasks.
     * @param ui The user interface used to display matching results.
     * @param storage The storage handler (not modified by this command).
     * @return The matching tasks string from the user interface.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList must not be null";
        assert ui != null : "Ui must not be null";
        assert storage != null : "Storage must not be null";
        assert this.targetDate != null : "Target date must not be null";

        List<Task> matchingTasks = tasks.findTasksOnDate(this.targetDate);
        String formattedDate = Time.formatDateForDisplay(this.targetDate);
        return ui.showTasksOnDate(matchingTasks, formattedDate);
    }
}
