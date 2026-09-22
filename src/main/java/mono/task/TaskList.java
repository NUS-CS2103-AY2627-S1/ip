package mono.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import mono.exception.NonExistentException;
import mono.storage.Storage;

/**
 * Owns Mono's task collection and all operations that change or display it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final Storage storage;

    /**
     * Creates a task list initialized from persisted tasks.
     */
    public TaskList() {
        this.storage = new Storage();
        Task[] storedTasks = this.storage.readStorage();
        assert storedTasks != null : "Storage must return a task array, even when empty";
        this.tasks = new ArrayList<>(Arrays.asList(storedTasks));
        assert this.tasks.stream().allMatch(Objects::nonNull)
                : "A task list must never contain null tasks";
    }

    /**
     * Adds and persists a task.
     *
     * @param task task to add
     * @return added task
     */
    public Task addTask(Task task) {
        assert task != null : "A task list can only contain real task objects";
        int previousSize = this.tasks.size();
        this.tasks.add(task);
        assert this.tasks.size() == previousSize + 1
                && this.tasks.get(previousSize) == task
                : "Adding a task must append that exact task to the list";
        saveTasks();
        return task;
    }

    /**
     * Deletes a task using a one-based ID.
     *
     * @param id one-based task ID
     * @return deleted task
     * @throws NonExistentException if the ID is invalid
     */
    public Task delete(int id) throws NonExistentException {
        validateTaskId(id);
        int zeroBasedIndex = id - 1;
        assert zeroBasedIndex >= 0 && zeroBasedIndex < this.tasks.size()
                : "Task ID validation must establish a safe zero-based index";
        Task task = this.tasks.remove(zeroBasedIndex);
        assert task != null : "A validated task-list entry must not be null";
        saveTasks();
        return task;
    }

    /**
     * Returns a snapshot of the current tasks.
     *
     * @return immutable task list
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }

    /**
     * Finds tasks whose names contain a keyword, preserving their list order.
     * Matching is case-sensitive.
     *
     * @param keyword text to find in each task name
     * @return immutable list of matching tasks
     * @throws NullPointerException if {@code keyword} is {@code null}
     */
    public List<Task> findTasks(String keyword) {
        Objects.requireNonNull(keyword, "keyword");
        return this.tasks.stream()
                .filter(task -> task.name.contains(keyword))
                .toList();
    }

    /**
     * Marks a task as completed and persists the change.
     *
     * @param id one-based task ID
     * @return task marked as completed
     * @throws NonExistentException if the ID is invalid
     */
    public Task markTaskDone(int id) throws NonExistentException {
        validateTaskId(id);
        Task task = this.tasks.get(id - 1);
        assert task != null : "A validated task-list entry must not be null";
        task.markDone();
        assert task.isDone : "markTaskDone must return a completed task";
        saveTasks();
        return task;
    }

    /**
     * Marks a task as incomplete and persists the change.
     *
     * @param id one-based task ID
     * @return task marked as incomplete
     * @throws NonExistentException if the ID is invalid
     */
    public Task unmarkTaskDone(int id) throws NonExistentException {
        validateTaskId(id);
        Task task = this.tasks.get(id - 1);
        assert task != null : "A validated task-list entry must not be null";
        task.unmarkDone();
        assert !task.isDone : "unmarkTaskDone must return an incomplete task";
        saveTasks();
        return task;
    }

    /**
     * Checks a one-based task ID.
     *
     * @param id one-based task ID
     * @throws NonExistentException if no task has that ID
     */
    private void validateTaskId(int id) throws NonExistentException {
        if (id < 1 || id > this.tasks.size()) {
            throw new NonExistentException("Task " + id + " does not exist.");
        }
        assert id >= 1 && id <= this.tasks.size()
                : "Successful task-ID validation must establish list bounds";
    }

    /**
     * Persists the complete current task collection.
     */
    private void saveTasks() {
        assert this.tasks.stream().allMatch(Objects::nonNull)
                : "Only non-null tasks may be persisted";
        this.storage.saveTasks(this.tasks.toArray(new Task[0]));
    }
}
