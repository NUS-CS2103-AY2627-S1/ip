package dude.task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import dude.storage.Storage;

/**
 * Receives task operations from commands and coordinates persistence.
 */
public final class TaskService {
    private final Storage storage;
    private TaskList tasks = new TaskList();

    /**
     * Creates a task receiver backed by the supplied storage handler.
     *
     * @param storage Persistence handler.
     */
    public TaskService(Storage storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    /**
     * Loads the persisted task list into this receiver.
     *
     * @throws IOException If persisted tasks cannot be read.
     */
    public void loadTasks() throws IOException {
        tasks = storage.loadTasks();
    }

    /**
     * Saves the receiver's current task list.
     *
     * @throws IOException If the task list cannot be persisted.
     */
    public void saveTasks() throws IOException {
        storage.saveTasks(tasks);
    }

    /**
     * Returns an immutable snapshot of the receiver's current tasks.
     *
     * @return Current tasks in display order.
     */
    public List<Task> tasks() {
        return tasks.asList();
    }

    /**
     * Returns tasks matching a description keyword.
     *
     * @param keyword Search keyword.
     * @return Matching tasks and their original positions.
     */
    public List<TaskMatch> findMatches(String keyword) {
        return tasks.findMatches(keyword);
    }

    /**
     * Returns indexes of tasks whose descriptions are duplicates of a description.
     *
     * @param description Description to compare.
     * @return Matching zero-based task indexes.
     */
    public List<Integer> findDuplicateIndexes(String description) {
        return tasks.findDuplicateIndexes(description);
    }

    /**
     * Adds a task to the receiver.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based index.
     *
     * @param index Zero-based insertion index.
     * @param task  Task to insert.
     */
    public void insert(int index, Task task) {
        tasks.insert(index, task);
    }

    /**
     * Removes and returns a task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces a task and returns the previous task.
     *
     * @param index Zero-based task index.
     * @param task  Replacement task.
     * @return Previous task.
     */
    public Task replace(int index, Task task) {
        return tasks.replace(index, task);
    }
}
