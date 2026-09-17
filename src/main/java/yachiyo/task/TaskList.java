package yachiyo.task;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import yachiyo.exception.YachiyoException;

/**
 * Manages Yachiyo's collection of tasks and operations on that collection.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     * A defensive copy prevents callers from modifying the internal collection.
     *
     * @param tasks initial tasks.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied tasks.
     * A defensive copy prevents callers from modifying the internal collection.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must not be null";
        assert tasks.stream().noneMatch(Objects::isNull)
                : "Initial task collection must not contain null";

        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";

        tasks.add(task);
    }

    /**
     * Returns the task with the supplied one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return matching task.
     * @throws YachiyoException if the task number is outside the list.
     */
    public Task get(int taskNumber) throws YachiyoException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Replaces the task with the supplied one-based task number.
     *
     * @param taskNumber one-based number of the task to replace.
     * @param replacementTask task that should take its place.
     * @throws YachiyoException if the task number is outside the list.
     */
    public void replace(int taskNumber, Task replacementTask) throws YachiyoException {
        assert replacementTask != null : "Replacement task must not be null";

        tasks.set(toIndex(taskNumber), replacementTask);
    }

    /**
     * Deletes and returns the task with the supplied one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return deleted task.
     * @throws YachiyoException if the task number is outside the list.
     */
    public Task delete(int taskNumber) throws YachiyoException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Checks whether the task list contains no tasks.
     *
     * @return true if the task list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return total task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Counts tasks that have not been completed.
     *
     * @return number of incomplete tasks.
     */
    public int getRemainingTaskCount() {
        return Math.toIntExact(tasks.stream()
                .filter(task -> !task.isCompleted())
                .count());
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword, retaining their original
     * task numbers. Matching is case-insensitive.
     *
     * @param keyword Keyword to match against task descriptions.
     * @return Matching numbered tasks.
     */
    public List<NumberedTask> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return getNumberedTasksMatching(task -> task.getDescription()
                .toLowerCase(Locale.ROOT)
                .contains(normalizedKeyword));
    }

    /**
     * Returns tasks that occur on the specified date, retaining their numbers
     * from the complete task list.
     *
     * @param date date to match.
     * @return matching numbered tasks.
     */
    public List<NumberedTask> getTasksOnDate(LocalDate date) {
        return getNumberedTasksMatching(task -> task.occursOn(date));
    }

    /**
     * Returns tasks satisfying the supplied condition with their original task numbers.
     *
     * @param condition condition used to select tasks.
     * @return matching numbered tasks.
     */
    private List<NumberedTask> getNumberedTasksMatching(Predicate<Task> condition) {
        List<NumberedTask> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (condition.test(task)) {
                matchingTasks.add(new NumberedTask(i + 1, task));
            }
        }
        return List.copyOf(matchingTasks);
    }

    /**
     * Returns an unmodifiable snapshot of the task collection.
     *
     * @return current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Converts a valid one-based task number to its zero-based list index.
     *
     * @param taskNumber one-based task number.
     * @return corresponding zero-based index.
     * @throws YachiyoException if the task number is outside the list.
     */
    private int toIndex(int taskNumber) throws YachiyoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YachiyoException(INVALID_OPERATION,
                    String.format("Hmm... choose a task number from 1 to %d, okay?", tasks.size())
            );
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size()
                : "Validated task number must map to an existing index";
        return index;
    }
}
