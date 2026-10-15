package grug.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the program's task list, with add and remove operations.
 */
public class TaskList {
    private List<Task> tasks;

    /**
     * Creates a new empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a new task list from a shallow copy of the tasks provided.
     *
     * @param tasks The initial task list to copy.
     */
    public TaskList(List<Task> tasks) {
        // copy into array list so this.tasks is modifiable even if tasks is not
        // also creates shallow copy so we don't accidentally modify another reference
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task list.
     *
     * @return The <b>unmodifiable</b> task list.
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }

    /**
     * Adds the task to the end of the task list.
     *
     * @param task The task to be added.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Removes the task from the task list. Guaranteed not to throw.
     *
     * @param index The index of the task to remove.
     * @return An {@link Optional} with the task removed present if the index is in
     *         range, an empty {@link Optional} otherwise.
     */
    public Optional<Task> removeTask(int index) {
        if (!this.isIndexInRange(index)) {
            return Optional.empty();
        }

        Task task = this.tasks.remove(index);
        return Optional.ofNullable(task);
    }

    /**
     * Gets the task from the task list. Guaranteed not the throw.
     *
     * @param index The index of the task.
     * @return An {@link Optional} with the task at the given index present if the
     *         index is in range, an empty {@link Optional} otherwise.
     */
    public Optional<Task> getTask(int index) {
        if (!this.isIndexInRange(index)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.tasks.get(index));
    }

    /**
     * Gets the task from the task list without checking that index is in range.
     *
     * @param index The index of the task.
     * @return The task at the given {@code index}.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Task getTaskUnchecked(int index) {
        return this.tasks.get(index);
    }

    /**
     * Checks whether the given index is in the range of the task list.
     *
     * @param index The index to check.
     * @return {@code true} if the index is in range, {@code false} otherwise.
     */
    public boolean isIndexInRange(int index) {
        return index >= 0 && index < this.tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return {@code true} if the task list is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.size() == 0;
    }

    /**
     * Returns the number of elements in the task list.
     *
     * @return The number of elements in the task list.
     */
    public int getSize() {
        return this.tasks.size();
    }
}
