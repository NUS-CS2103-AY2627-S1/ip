package duke;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Owns Wangsa's ordered task collection and the operations that change it.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
        assertInvariant();
    }

    /**
     * Creates a task list containing the supplied saved tasks.
     *
     * @param savedTasks Tasks loaded from storage.
     * @throws WangsaException If the saved list is invalid or exceeds Wangsa's capacity.
     */
    public TaskList(List<Task> savedTasks) throws WangsaException {
        if (savedTasks == null) {
            throw new WangsaException("The saved task list cannot be null.");
        }
        if (savedTasks.size() > MAX_TASKS) {
            throw new WangsaException("The saved task list contains more than "
                    + MAX_TASKS + " tasks.");
        }
        for (Task task : savedTasks) {
            validateTask(task);
        }
        this.tasks = new ArrayList<>(savedTasks);
        assertInvariant();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the current task order that callers cannot add to or reorder.
     *
     * <p>The task objects are shared with this list, so later status changes remain visible.</p>
     *
     * @return An unmodifiable list containing the current task objects.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the task at a one-based display position.
     *
     * @param taskNumber One-based task number.
     * @return The task at the requested position.
     * @throws WangsaException If the number is invalid.
     */
    public Task get(int taskNumber) throws WangsaException {
        return getTask(taskNumber);
    }

    /**
     * Adds and returns a task, provided the list still has capacity.
     *
     * @param task Task to add.
     * @return The added task.
     * @throws WangsaException If the task is invalid or the list is full.
     */
    public Task add(Task task) throws WangsaException {
        validateTask(task);
        if (tasks.size() >= MAX_TASKS) {
            throw new WangsaException("Your task list is full (maximum 100 tasks).");
        }
        tasks.add(task);
        assertInvariant();
        return task;
    }

    /**
     * Adds several tasks as one capacity-checked operation.
     *
     * @param newTasks Tasks to add.
     * @throws WangsaException If a task is invalid or the combined list would exceed capacity.
     */
    public void addAll(Task... newTasks) throws WangsaException {
        if (newTasks == null) {
            throw new WangsaException("Tasks to add cannot be null.");
        }
        for (Task task : newTasks) {
            validateTask(task);
        }
        if (newTasks.length > MAX_TASKS - tasks.size()) {
            throw new WangsaException("Your task list is full (maximum 100 tasks).");
        }
        tasks.addAll(Arrays.asList(newTasks));
        assertInvariant();
    }

    /**
     * Marks and returns the numbered task as done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws WangsaException If the number is invalid.
     */
    public Task mark(int taskNumber) throws WangsaException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the numbered task as not done.
     *
     * @param taskNumber One-based task number.
     * @return The updated task.
     * @throws WangsaException If the number is invalid.
     */
    public Task unmark(int taskNumber) throws WangsaException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes and returns the numbered task.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws WangsaException If the number is invalid.
     */
    public Task delete(int taskNumber) throws WangsaException {
        getTask(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Finds tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword Text to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return findMatches(keyword).stream().map(TaskMatch::task).toList();
    }

    /**
     * Finds matching descriptions while preserving full-list task numbers.
     *
     * @param keyword Text to search for, ignoring case.
     * @return Matching tasks with their current one-based positions.
     */
    public List<TaskMatch> findMatches(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .mapToObj(index -> new TaskMatch(index + 1, tasks.get(index)))
                .toList();
    }

    /**
     * Sorts deadlines chronologically and keeps tasks without deadlines after them.
     */
    public void sortByDeadline() {
        tasks.sort(Comparator.comparing(this::getSortDate));
    }

    /**
     * Restores a previously saved task order after a failed persistence operation.
     */
    void restoreOrder(List<Task> previousOrder) {
        tasks.clear();
        tasks.addAll(previousOrder);
        assertInvariant();
    }

    /**
     * Resolves a one-based task number or reports that it is outside the list.
     */
    private Task getTask(int taskNumber) throws WangsaException {
        if (tasks.isEmpty()) {
            throw new WangsaException("Your list is empty. Add a task before choosing a task number.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new WangsaException("Choose a task number between 1 and "
                    + tasks.size() + ". Type `list` to check your tasks.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Rejects incomplete tasks before they can enter the domain collection.
     */
    private void validateTask(Task task) throws WangsaException {
        if (task == null) {
            throw new WangsaException("A task cannot be null.");
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            throw new WangsaException("A task description cannot be blank.");
        }
        if (task instanceof Deadline deadline && deadline.getBy() == null) {
            throw new WangsaException("A deadline must have a date.");
        }
        if (task instanceof Deadline deadline && (deadline.getBy().getYear() < 0
                || deadline.getBy().getYear() > 9999)) {
            throw new WangsaException("A deadline year must have four digits (0000 to 9999).");
        }
        if (task instanceof Event event && (event.getFrom() == null || event.getFrom().isBlank()
                || event.getTo() == null || event.getTo().isBlank())) {
            throw new WangsaException("An event must have a start and an end.");
        }
    }

    /**
     * Returns a task's deadline or a sentinel date that sorts undated tasks last.
     */
    private LocalDate getSortDate(Task task) {
        return task instanceof Deadline deadline ? deadline.getBy() : LocalDate.MAX;
    }

    /**
     * Checks invariants that should hold for every task-list state.
     */
    private void assertInvariant() {
        assert tasks.size() <= MAX_TASKS : "task list cannot exceed its capacity";
    }
}
