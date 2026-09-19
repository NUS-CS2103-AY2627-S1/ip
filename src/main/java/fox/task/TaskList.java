package fox.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import fox.exception.FoxException;

/**
 * Encapsulates Fox's ordered collection of tasks and its capacity rules.
 */
public class TaskList implements Iterable<Task> {
    private final int capacity;
    private final List<Task> tasks;

    /**
     * Creates an empty task list with the given maximum capacity.
     *
     * @param capacity the maximum number of tasks; must not be negative
     * @throws IllegalArgumentException if {@code capacity} is negative
     */
    public TaskList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Task list capacity cannot be negative.");
        }
        this.capacity = capacity;
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param capacity the maximum number of tasks; must not be negative
     * @param initialTasks the zero or more tasks to place in their existing order
     * @throws FoxException if the supplied tasks exceed {@code capacity}
     * @throws NullPointerException if {@code initialTasks} is {@code null}
     */
    public TaskList(int capacity, Task... initialTasks) throws FoxException {
        this(capacity);
        if (initialTasks.length > capacity) {
            throw new FoxException("The data file contains more than " + capacity + " tasks.");
        }
        for (Task task : initialTasks) {
            add(task);
        }
    }

    /**
     * Adds a unique task, rejecting duplicates and additions beyond the configured capacity.
     *
     * @param task the task to append; may be {@code null}, although callers normally provide a task
     * @throws FoxException if the task duplicates an existing task or the list has reached its capacity
     */
    public void add(Task task) throws FoxException {
        assert tasks.size() <= capacity : "Task count must not exceed capacity";

        if (task == null) {
            throw new FoxException("☹ OOPS!!! A task cannot be missing.");
        }
        if (containsTaskWithSameDetails(task)) {
            throw new FoxException("☹ OOPS!!! This task is already in your task list.");
        }
        if (tasks.size() == capacity) {
            throw new FoxException("☹ OOPS!!! Your task list is full.");
        }
        tasks.add(task);
        assert tasks.size() <= capacity : "Task count must not exceed capacity";
    }

    /** Returns whether the list contains a non-null task with the same intrinsic details. */
    private boolean containsTaskWithSameDetails(Task task) {
        return tasks.stream()
                .filter(Objects::nonNull)
                .anyMatch(task::hasSameDetails);
    }

    /**
     * Returns the task using the user's one-based task number.
     *
     * @param taskNumber the one-based position of the requested task
     * @return the task at {@code taskNumber}
     * @throws FoxException if {@code taskNumber} is outside the current list
     */
    public Task get(int taskNumber) throws FoxException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new FoxException("☹ OOPS!!! I couldn't find that task.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Marks the selected task as done.
     *
     * @param taskNumber the one-based position of the task to update
     * @return the updated task
     * @throws FoxException if {@code taskNumber} is outside the current list
     */
    public Task markDone(int taskNumber) throws FoxException {
        Task task = get(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the selected task as not done.
     *
     * @param taskNumber the one-based position of the task to update
     * @return the updated task
     * @throws FoxException if {@code taskNumber} is outside the current list
     */
    public Task markNotDone(int taskNumber) throws FoxException {
        Task task = get(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the selected task.
     *
     * @param taskNumber the one-based position of the task to remove
     * @return the removed task
     * @throws FoxException if {@code taskNumber} is outside the current list
     */
    public Task delete(int taskNumber) throws FoxException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new FoxException("☹ OOPS!!! I couldn't find that task.");
        }
        Task deletedTask = tasks.remove(taskNumber - 1);
        assert tasks.size() <= capacity : "Task count must not exceed capacity";
        return deletedTask;
    }

    /**
     * Returns the original one-based numbers of tasks matching at least one keyword as a whole word.
     * Matching is case-insensitive so that command capitalization does not affect results.
     *
     * @param keywords the one or more search keywords
     * @return matching task numbers in their existing list order
     */
    public List<Integer> findMatchingTaskNumbers(String... keywords) {
        List<Pattern> keywordPatterns = new ArrayList<>();
        for (String keyword : keywords) {
            String wholeWordPattern = "(?iu)(?<![\\p{L}\\p{N}_])" + Pattern.quote(keyword)
                    + "(?![\\p{L}\\p{N}_])";
            keywordPatterns.add(Pattern.compile(wholeWordPattern));
        }

        List<Integer> matchingTaskNumbers = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            String description = tasks.get(index).getDescription();
            boolean hasMatchingKeyword = keywordPatterns.stream()
                    .anyMatch(pattern -> pattern.matcher(description).find());
            if (hasMatchingKeyword) {
                matchingTaskNumbers.add(index + 1);
            }
        }
        return matchingTaskNumbers;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the current number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot suitable for persistence.
     *
     * @return a new array containing the tasks in list order
     */
    public Task[] toArray() {
        return tasks.toArray(new Task[0]);
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return an iterator backed by this list
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
