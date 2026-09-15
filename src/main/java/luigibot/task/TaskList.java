package luigibot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Owns and manages LuigiBot's collection of tasks.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "initial task collection should not be null";
        assert hasNoNullTasks(tasks) : "initial task collection should not contain null";

        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "task to add should not be null";

        this.tasks.add(task);
    }

    /**
     * Removes and returns the task with the given displayed task number.
     *
     * @param taskNumber displayed task number, starting from 1.
     * @return removed task.
     * @throws IndexOutOfBoundsException if the task number does not identify a stored task.
     */
    public Task delete(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "task number should identify a stored task";

        return this.tasks.remove(taskNumber - 1);
    }

    /**
     * Marks the task with the given displayed task number as done.
     *
     * @param taskNumber displayed task number, starting from 1.
     * @return task that was marked.
     * @throws IndexOutOfBoundsException if the task number does not identify a stored task.
     */
    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the task with the given displayed task number as not done.
     *
     * @param taskNumber displayed task number, starting from 1.
     * @return task that was unmarked.
     * @throws IndexOutOfBoundsException if the task number does not identify a stored task.
     */
    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Returns whether a displayed task number identifies a stored task.
     *
     * @param taskNumber displayed task number.
     * @return true when the task number is valid.
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= this.tasks.size();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns a read-only view of all stored tasks.
     *
     * @return unmodifiable task view.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Returns the indexes of tasks that occur on the given date.
     *
     * @param date date to search.
     * @return zero-based indexes of matching tasks.
     */
    public List<Integer> findIndexesOnDate(LocalDate date) {
        assert date != null : "search date should not be null";

        return IntStream.range(0, this.tasks.size())
                .filter(index -> this.tasks.get(index).occursOn(date))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the indexes of tasks whose descriptions contain the keyword.
     *
     * @param keyword keyword to search for, ignoring case.
     * @return zero-based indexes of matching tasks.
     */
    public List<Integer> findIndexesByKeyword(String keyword) {
        assert keyword != null : "search keyword should not be null";
        assert !keyword.isBlank() : "search keyword should not be blank";

        return IntStream.range(0, this.tasks.size())
                .filter(index -> this.tasks.get(index).matchesDescription(keyword))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the indexes of events that overlap with the given event.
     * Tasks without a time period, such as todos and deadlines, are not included.
     *
     * @param event event to compare against.
     * @return zero-based indexes of clashing events.
     */
    public List<Integer> findIndexesClashingWith(Event event) {
        assert event != null : "event to compare should not be null";

        return IntStream.range(0, this.tasks.size())
                .filter(index -> this.tasks.get(index) instanceof Event existingEvent
                        && event.overlaps(existingEvent))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the task with the given displayed task number.
     *
     * @param taskNumber displayed task number, starting from 1.
     * @return selected task.
     */
    private Task getTask(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "task number should identify a stored task";

        return this.tasks.get(taskNumber - 1);
    }

    /**
     * Returns whether the given collection contains no null tasks.
     *
     * @param tasks tasks to inspect.
     * @return true when every task is non-null.
     */
    private static boolean hasNoNullTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task == null) {
                return false;
            }
        }
        return true;
    }
}
