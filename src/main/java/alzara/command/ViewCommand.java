package alzara.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Finds every task scheduled on a given date and displays them sorted
 * chronologically.
 */
public class ViewCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that shows every task scheduled on {@code date}.
     *
     * @param date the date to query
     */
    public ViewCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Filters {@code memory} down to tasks scheduled on this command's date,
     * sorts them chronologically, and displays them. Read-only - unlike
     * {@link AddCommand}/{@link DeleteCommand}/{@link MarkCommand}, this never
     * calls {@code storage.save}, since it doesn't change the task list.
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) {
        ArrayList<Task> scheduled = memory.getTasks().stream()
                .filter(task -> task.isScheduledOn(date))
                .sorted(Comparator.comparing(Task::getSortDate))
                .collect(Collectors.toCollection(ArrayList::new));
        ui.showScheduledTasks(date, scheduled);
    }
}
