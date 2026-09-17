package crystal.command;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskDateTime;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Displays either the complete task list or a temporary date-filtered view.
 */
public final class ListCommand extends Command {
    /** Optional date used to filter deadlines and events. */
    private final Optional<LocalDate> date;

    /**
     * Creates a list command with an optional date filter.
     *
     * @param date empty for the complete list, or the requested filter date.
     */
    public ListCommand(Optional<LocalDate> date) {
        this.date = date;
    }

    /**
     * Displays the requested task-list view without changing persistent data.
     *
     * @param tasks task list to display.
     * @param ui console interface used to present the result.
     * @param storage persistence service; not used by this read-only command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (date.isEmpty()) {
            ui.showTaskList(tasks);
            return;
        }

        LocalDate filterDate = date.get();
        List<Task> matchingTasks = tasks.getTasksOnDate(filterDate);
        String formattedDate = TaskDateTime.formatDate(filterDate);
        ui.showTasksOnDate(formattedDate, matchingTasks);
    }
}
