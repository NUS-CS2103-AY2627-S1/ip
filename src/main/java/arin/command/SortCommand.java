package arin.command;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import arin.exception.ArinException;
import arin.storage.Storage;
import arin.task.Deadline;
import arin.task.Task;
import arin.ui.Ui;

/**
 * Reorders tasks by description or deadline and saves their new positions.
 */
public class SortCommand extends Command {
    private final Comparator<Task> comparator;

    /**
     * Creates a command for one of the supported sort orders.
     *
     * @param sortBy Trimmed sort option, either {@code name} or {@code deadline}.
     * @throws ArinException If the sort option is missing or unsupported.
     */
    public SortCommand(String sortBy) throws ArinException {
        if (sortBy.equals("name")) {
            comparator = Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
        } else if (sortBy.equals("deadline")) {
            comparator = Comparator.comparing(SortCommand::getDeadlineDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            throw new ArinException("Use: sort name OR sort deadline.");
        }
    }

    /**
     * Saves and displays a stable ordering without changing task details.
     * Tasks with equal keys keep their relative positions. For deadline sorting,
     * tasks without a deadline follow all deadlines in their existing order.
     *
     * @param tasks Mutable list whose positions will be updated after saving succeeds.
     * @param ui User interface for displaying the new task numbers.
     * @param storage Persistent task storage.
     * @throws IOException If saving fails; the in-memory order is left unchanged.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui, Storage storage) throws IOException {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(comparator);
        storage.saveTasks(sortedTasks);

        tasks.clear();
        tasks.addAll(sortedTasks);
        ui.showTaskList(tasks);
    }

    /**
     * Extracts a deadline's date without interpreting free-text event times.
     *
     * @param task Task to inspect.
     * @return Deadline date, or {@code null} for a task without a deadline.
     */
    private static LocalDate getDeadlineDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDate();
        }
        return null;
    }
}
