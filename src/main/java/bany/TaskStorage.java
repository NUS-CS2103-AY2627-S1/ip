package bany;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import bany.tasks.Task;

/** Maintains the in-memory ordered list of tasks used by Bany. */
public class TaskStorage {
    /**
     * Tasks in the order in which they are displayed and indexed.
     */
    private final List<Task> taskList;

    /**
     * Creates an empty task list.
     */
    public TaskStorage() {
        this.taskList = new ArrayList<>(100);
    }

    /**
     * Adds a task while enforcing non-null and unique task IDs.
     *
     * @param task task to add.
     * @throws NullPointerException     if {@code task} is null.
     * @throws IllegalArgumentException if another task has the same ID.
     */
    public void addTask(Task task) {
        Task nonNullTask = Objects.requireNonNull(task, "Task cannot be null.");
        if (taskList.stream().anyMatch(existing -> existing.getId() == nonNullTask.getId())) {
            throw new IllegalArgumentException("Task ID must be unique.");
        }
        taskList.add(nonNullTask);
    }

    /**
     * Replaces the current list with tasks loaded from storage.
     *
     * @param tasks tasks restored from the data file.
     * @throws NullPointerException     if {@code tasks} is null.
     * @throws IllegalArgumentException if the list contains a null task or duplicate ID.
     */
    public void replaceTasks(List<Task> tasks) {
        Objects.requireNonNull(tasks, "Task list cannot be null.");
        Set<Integer> ids = new HashSet<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("Task list cannot contain null tasks.");
            }
            if (!ids.add(task.getId())) {
                throw new IllegalArgumentException("Task IDs must be unique.");
            }
        }
        taskList.clear();
        taskList.addAll(tasks);
    }

    /**
     * Returns the task at a zero-based list index.
     *
     * @param index zero-based task index.
     * @return an optional containing the task, or {@link Optional#empty()} if the index is invalid.
     */
    public Optional<Task> getTask(int index) {
        if (index < 0 || index >= taskList.size()) {
            return Optional.empty();
        }
        Task task = taskList.get(index);
        // addTask and replaceTasks reject null tasks, so a valid stored position
        // must always resolve to an actual task rather than a nullable entry.
        assert task != null : "Task storage must not contain null tasks.";
        return Optional.of(task);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return current task count.
     */
    public int getSize() {
        return taskList.size();
    }

    /**
     * Returns an immutable snapshot of the stored tasks.
     *
     * @return tasks in display order.
     */
    public List<Task> getTasks() {
        return List.copyOf(taskList);
    }

    /**
     * Marks a task at a zero-based index as done.
     *
     * @param taskNo zero-based task index.
     * @return the marked task, or {@code null} if the index is invalid.
     */
    public Task markTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = taskList.get(taskNo);
        task.mark();
        return task;
    }

    /**
     * Marks a task at a zero-based index as not done.
     *
     * @param taskNo zero-based task index.
     * @return the unmarked task, or {@code null} if the index is invalid.
     */
    public Task unmarkTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = taskList.get(taskNo);
        task.unmark();
        return task;
    }

    /**
     * Deletes a task at a zero-based index.
     *
     * @param taskNo zero-based task index.
     * @return the deleted task, or {@code null} if the index is invalid.
     */
    public Task deleteTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = taskList.get(taskNo);
        taskList.remove(task);
        return task;
    }

    /**
     * Inserts a task at a specific list position, such as when undoing a failed deletion.
     *
     * @param index zero-based insertion position.
     * @param task task to insert.
     * @throws NullPointerException if {@code task} is null.
     * @throws IndexOutOfBoundsException if the index is outside the insertion range.
     */
    public void insertTask(int index, Task task) {
        Objects.requireNonNull(task, "Task cannot be null.");
        if (index < 0 || index > getSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %d out of bounds for insertion into size %d", index, getSize()));
        }
        taskList.add(index, task);
    }
}
