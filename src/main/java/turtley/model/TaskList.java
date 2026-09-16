package turtley.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import turtley.exception.TurtleyException;

/**
 * Owns Turtley's ordered collection of tasks and its basic list operations.
 */
public class TaskList {

    private static final int MAX_TASK_COUNT = 100;
    private static final String TASK_LIMIT_MESSAGE = "Task list exceeds " + MAX_TASK_COUNT + " tasks.";
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        assertInvariants();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage.
     */
    public TaskList(List<Task> initialTasks) {
        addAll(initialTasks);
    }

    /**
     * Returns whether this list has reached its supported capacity.
     *
     * @return {@code true} when the list reaches its supported capacity.
     */
    public boolean isFull() {
        return tasks.size() >= MAX_TASK_COUNT;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list has no tasks.
     *
     * @return {@code true} when the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the supplied zero-based index.
     *
     * @param index the zero-based task index.
     * @return the task at that index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     * @throws TurtleyException if the list is full, the task is null, or its details are duplicated.
     */
    public void add(Task task) {
        if (task == null) {
            throw new TurtleyException("Cannot add a null task.");
        }
        if (isFull()) {
            throw new TurtleyException(TASK_LIMIT_MESSAGE);
        }
        ensureNotDuplicate(task);
        tasks.add(task);
        assertInvariants();
    }

    /**
     * Inserts a task at the supplied zero-based index.
     *
     * @param index the insertion index.
     * @param task the task to insert.
     * @throws TurtleyException if the list is full, the task is null, or its details are duplicated.
     */
    public void add(int index, Task task) {
        if (task == null) {
            throw new TurtleyException("Cannot add a null task.");
        }
        if (isFull()) {
            throw new TurtleyException(TASK_LIMIT_MESSAGE);
        }
        ensureNotDuplicate(task);
        tasks.add(index, task);
        assertInvariants();
    }

    /**
     * Adds all supplied tasks to the end of the list.
     *
     * @param newTasks the tasks to add.
     * @throws TurtleyException if the tasks are null, contain null or duplicate details, or exceed capacity.
     */
    public void addAll(List<Task> newTasks) {
        if (newTasks == null || newTasks.stream().anyMatch(task -> task == null)) {
            throw new TurtleyException("Cannot add null tasks.");
        }
        if (tasks.size() + newTasks.size() > MAX_TASK_COUNT) {
            throw new TurtleyException(TASK_LIMIT_MESSAGE);
        }
        List<Task> combinedTasks = new ArrayList<>(tasks);
        for (Task task : newTasks) {
            ensureNotDuplicate(task, combinedTasks);
            combinedTasks.add(task);
        }
        tasks.addAll(newTasks);
        assertInvariants();
    }

    private void ensureNotDuplicate(Task task) {
        ensureNotDuplicate(task, tasks);
    }

    private static void ensureNotDuplicate(Task task, List<Task> existingTasks) {
        if (existingTasks.stream().anyMatch(existingTask -> hasSameDetails(existingTask, task))) {
            throw new TurtleyException("Cannot add a duplicate task.");
        }
    }

    private static boolean hasSameDetails(Task first, Task second) {
        if (first.getTaskType() != second.getTaskType()
                || !Objects.equals(first.getDescription(), second.getDescription())
                || !Objects.equals(first.getTags(), second.getTags())) {
            return false;
        }
        if (first instanceof Deadline && second instanceof Deadline) {
            return Objects.equals(((Deadline) first).getBy(), ((Deadline) second).getBy());
        }
        if (first instanceof Event && second instanceof Event) {
            Event firstEvent = (Event) first;
            Event secondEvent = (Event) second;
            return Objects.equals(firstEvent.getFrom(), secondEvent.getFrom())
                    && Objects.equals(firstEvent.getTo(), secondEvent.getTo());
        }
        return true;
    }

    /**
     * Removes and returns the task at the supplied zero-based index.
     *
     * @param index the zero-based task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        Task removedTask = tasks.remove(index);
        assert removedTask != null : "TaskList must never contain null tasks.";
        assertInvariants();
        return removedTask;
    }

    /**
     * Provides a read-only view for collaborators such as Storage.
     *
     * @return an unmodifiable view of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks the invariants maintained by this task list.
     *
     * <p>Assertions are used because these conditions are guaranteed by the list's own mutation
     * methods and indicate a programming error if they ever fail.</p>
     */
    private void assertInvariants() {
        assert tasks.size() <= MAX_TASK_COUNT : "TaskList must not exceed its maximum capacity.";
        assert tasks.stream().allMatch(task -> task != null) : "TaskList must not contain null tasks.";
    }
}
