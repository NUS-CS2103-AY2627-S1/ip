package michael;

import task.Task;
import task.Deadline;
import task.Event;
import task.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the task list and handles list-manipulation operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a {@code TaskList} initialized with a given list of tasks.
     *
     * @param tasks Initial list of tasks to populate the task list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates an independent copy of this list, including each task's completion state.
     *
     * @return a snapshot that can safely be used to restore this list later
     * @throws MichaelException if a task cannot be copied
     */
    public TaskList snapshot() throws MichaelException {
        List<Task> copiedTasks = new ArrayList<>();
        for (Task task : tasks) {
            Task copy;
            if (task instanceof Deadline deadline) {
                copy = new Deadline(deadline.getDescription(), deadline.getBy());
            } else if (task instanceof Event event) {
                copy = new Event(event.getDescription(), event.getFrom(), event.getTo());
            } else if (task instanceof Todo) {
                copy = new Todo(task.getDescription());
            } else {
                throw new MichaelException("Unable to create an undo snapshot.");
            }
            if (task.getStatusIcon().equals("X")) {
                copy.markAsDone();
            }
            copiedTasks.add(copy);
        }
        return new TaskList(copiedTasks);
    }

    /**
     * Adds a new task to the end of the list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index The zero-based index of the task to delete.
     * @return The task that was removed.
     * @throws MichaelException If the specified index is out of bounds.
     */
    public Task delete(int index) throws MichaelException {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index The zero-based index of the task to unmark.
     * @return The task that was marked as incomplete.
     * @throws MichaelException If the specified index is out of bounds.
     */
    public Task mark(int index) throws MichaelException {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Retrieves the task at the specified index without removing it.
     *
     * @param index The zero-based index of the task to retrieve.
     * @return The task at the specified index.
     * @throws MichaelException If the specified index is out of bounds.
     */
    public Task unmark(int index) throws MichaelException {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Retrieves the task at the specified index without removing it.
     *
     * @param index The zero-based index of the task to retrieve.
     * @return The task at the specified index.
     * @throws MichaelException If the specified index is out of bounds.
     */
    public Task get(int index) throws MichaelException {
        validateIndex(index);
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the tasks.
     *
     * @return an unmodifiable list containing all tasks
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Validates whether a given index falls within the valid bounds of the task list.
     *
     * @param index The index to check.
     * @throws MichaelException If the index is negative or greater than/equal to the list size.
     */
    private void validateIndex(int index) throws MichaelException {
        if (index < 0 || index >= tasks.size()) {
            throw new MichaelException("That task number is not in the list.");
        }
        assert index >= 0 && index < tasks.size()
                : "A validated task index must be within the list bounds";
    }
    /**
     * Finds and returns a list of tasks that contain the specified keyword in their description.
     *
     * @param keyword The substring to search for within task descriptions.
     * @return A list of tasks matching the keyword.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }
}
