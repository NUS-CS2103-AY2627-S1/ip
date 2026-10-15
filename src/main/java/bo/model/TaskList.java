package bo.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Owns the tasks currently tracked by Bo and operations on that collection.
 */
public final class TaskList {
    /** Maximum number of tasks supported by the current application. */
    public static final int DEFAULT_CAPACITY = 100;

    /** The backing array containing the tasks in list order. */
    private final Task[] tasks;

    /** Number of occupied positions in {@link #tasks}. */
    private int taskCount;

    /** Creates an empty task list with the default capacity. */
    public TaskList() {
        this.tasks = new Task[DEFAULT_CAPACITY];
        this.taskCount = 0;
    }

    /**
     * Creates a task list initialized from tasks loaded by storage.
     *
     * @param initialTasks the array containing loaded tasks.
     * @param initialTaskCount number of valid tasks in {@code initialTasks}.
     * @throws IllegalArgumentException if the initial array or count is invalid.
     */
    public TaskList(Task[] initialTasks, int initialTaskCount) {
        if (initialTasks == null || initialTaskCount < 0
                || initialTaskCount > initialTasks.length) {
            throw new IllegalArgumentException("The initial tasks are invalid.");
        }
        for (int i = 0; i < initialTaskCount; i++) {
            if (initialTasks[i] == null) {
                throw new IllegalArgumentException("The initial tasks cannot contain null entries.");
            }
        }
        this.tasks = Arrays.copyOf(initialTasks, initialTasks.length);
        this.taskCount = initialTaskCount;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return taskCount;
    }

    /**
     * Returns whether this list cannot accept another task.
     *
     * @return {@code true} when the list has reached its capacity.
     */
    public boolean isFull() {
        return taskCount == tasks.length;
    }

    /**
     * Adds a task when capacity is available.
     *
     * @param task the task to add.
     * @return {@code true} when the task was added, or {@code false} when full.
     */
    public boolean add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task cannot be null.");
        }
        if (isFull()) {
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index.
     * @return the task at that index.
     * @throws IndexOutOfBoundsException if the index is outside this list.
     */
    public Task get(int index) {
        checkIndex(index);
        return tasks[index];
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index.
     * @return the removed task.
     * @throws IndexOutOfBoundsException if the index is outside this list.
     */
    public Task remove(int index) {
        checkIndex(index);
        Task removedTask = tasks[index];
        int tasksToShift = taskCount - index - 1;
        if (tasksToShift > 0) {
            System.arraycopy(tasks, index + 1, tasks, index, tasksToShift);
        }
        taskCount--;
        tasks[taskCount] = null;
        return removedTask;
    }

    /**
     * Returns a copy containing the current tasks for storage to serialize.
     *
     * @return an array containing exactly the current tasks.
     */
    public Task[] toArray() {
        return Arrays.copyOf(tasks, taskCount);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring case.
     * Matching tasks retain their original list order.
     *
     * @param keyword the text to search for
     * @return an array containing the matching tasks
     * @throws IllegalArgumentException if the keyword is null or blank
     */
    public Task[] find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("A search keyword cannot be blank.");
        }

        String normalizedKeyword = keyword.strip().toLowerCase(Locale.ROOT);
        Task[] matchingTasks = new Task[taskCount];
        int matchingTaskCount = 0;
        for (int i = 0; i < taskCount; i++) {
            String description = tasks[i].getDescription();
            if (description != null
                    && description.toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks[matchingTaskCount] = tasks[i];
                matchingTaskCount++;
            }
        }
        return Arrays.copyOf(matchingTasks, matchingTaskCount);
    }

    /**
     * Sorts tasks by their relevant date in ascending order.
     *
     * <p>Deadlines use their deadline, events use their start time, and tasks
     * without a typed date are placed last. Tasks with equal sort dates retain
     * their original relative order.</p>
     */
    public void sortByDate() {
        Arrays.sort(tasks, 0, taskCount,
                Comparator.comparing(TaskList::getSortDate,
                        Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /** Returns the date used to order a task, or {@code null} when it has none. */
    private static LocalDateTime getSortDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }

    /** Checks an index before accessing the backing array. */
    private void checkIndex(int index) {
        if (index < 0 || index >= taskCount) {
            throw new IndexOutOfBoundsException("Task index: " + index);
        }
    }
}
