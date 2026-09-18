package geek.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import geek.exception.GeekException;

/**
 * Maintains tasks and applies user-facing operations using one-based numbers.
 */
public class TaskList {
    /**
     * Mutable internal list protected by defensive copies at the class
     * boundary.
     */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied list.
     *
     * @param tasks Initial tasks in display order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task with the specified one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Removed task.
     * @throws GeekException If the task number is outside the list.
     */
    public Task delete(int taskNumber) {
        checkTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks and returns the task with the specified one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Task that was marked.
     * @throws GeekException If the task number is outside the list.
     */
    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Unmarks and returns the task with the specified one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Task that was unmarked.
     * @throws GeekException If the task number is outside the list.
     */
    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Finds dated tasks occurring on the specified date.
     *
     * @param date Date to search for.
     * @return Unmodifiable result list preserving the task-list order.
     */
    public List<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword,
     * ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Unmodifiable result list preserving task-list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Sorts dated tasks from earliest to latest and places undated tasks last.
     *
     * Tasks with the same date-time, as well as undated tasks, retain their
     * relative order because {@link List#sort} is stable.
     */
    public void sortChronologically() {
        tasks.sort(TaskList::compareChronologically);
    }

    /**
     * Returns an unmodifiable snapshot of the current task list.
     *
     * The task objects themselves are not copied.
     *
     * @return Snapshot of the tasks in display order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task with the specified one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Matching task.
     * @throws GeekException If the task number is outside the list.
     */
    private Task getTask(int taskNumber) {
        checkTaskNumber(taskNumber);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "A validated task number must identify a task.";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Verifies that a one-based task number identifies an existing task.
     *
     * @param taskNumber Task number to check.
     * @throws GeekException If the number is outside the list.
     */
    private void checkTaskNumber(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GeekException(
                    "That task number does not exist."
            );
        }
    }

    private static int compareChronologically(
            Task first,
            Task second
    ) {
        Optional<LocalDateTime> firstDateTime =
                first.getSortDateTime();
        Optional<LocalDateTime> secondDateTime =
                second.getSortDateTime();

        if (firstDateTime.isEmpty()) {
            return secondDateTime.isEmpty() ? 0 : 1;
        }

        if (secondDateTime.isEmpty()) {
            return -1;
        }

        return firstDateTime.orElseThrow()
                .compareTo(secondDateTime.orElseThrow());
    }
}
