package duke.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;

import duke.exception.DukeException;

/**
 * Encapsulates the collection of tasks and operations on the task list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final Deque<ArrayList<Task>> history = new ArrayDeque<>();

    /**
     * Constructs a TaskList initialized with the given tasks.
     *
     * @param tasks The initial tasks to populate the list with.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "Initial tasks array must not be null";
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    /**
     * Constructs a TaskList initialized with the given list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial tasks list must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Adds one or more tasks to the task list.
     *
     * @param tasksToAdd The tasks to add to the list.
     */
    public void addTasks(Task... tasksToAdd) {
        assert tasksToAdd != null : "Tasks array must not be null";
        for (Task task : tasksToAdd) {
            assert task != null : "Task to add must not be null";
        }
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Checks if the task list contains the specified task.
     *
     * @param task The task to search for.
     * @return True if the task list contains the task, false otherwise.
     */
    public boolean contains(Task task) {
        assert task != null : "Task to check must not be null";
        return tasks.contains(task);
    }

    /**
     * Deletes a task from the list at the specified index.
     *
     * @param index The 0-based index of the task to delete.
     * @return The removed task.
     * @throws DukeException If the index is out of bounds.
     */
    public Task deleteTask(int index) throws DukeException {
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("OOPS!!! The task number is invalid.");
        }
        return tasks.remove(index);
    }

    /**
     * Retrieves a task from the list at the specified index.
     *
     * @param index The 0-based index of the task to retrieve.
     * @return The task at the specified index.
     * @throws DukeException If the index is out of bounds.
     */
    public Task getTask(int index) throws DukeException {
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("OOPS!!! The task number is invalid.");
        }
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The ArrayList of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds all tasks whose description contains the specified keyword.
     *
     * @param keyword The search keyword.
     * @return A new TaskList containing matching tasks.
     */
    public TaskList findTasks(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.toString().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matchingTasks);
    }

    /**
     * Saves the current list state to the history stack for undo operations.
     */
    public void saveSnapshot() {
        ArrayList<Task> snapshot = new ArrayList<>();
        for (Task task : tasks) {
            snapshot.add(task.copy());
        }
        history.push(snapshot);
    }

    /**
     * Reverts the task list to its previous state.
     *
     * @throws DukeException If there is no previous state to revert to.
     */
    public void undo() throws DukeException {
        if (history.isEmpty()) {
            throw new DukeException("OOPS!!! There are no previous commands to undo.");
        }
        ArrayList<Task> previousState = history.pop();
        tasks.clear();
        tasks.addAll(previousState);
    }

    /**
     * Returns whether there is an undo state available in history.
     *
     * @return True if undo is possible, false otherwise.
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * Creates and returns a deep copy of this TaskList.
     *
     * @return A deep copy of this TaskList.
     */
    public TaskList copy() {
        ArrayList<Task> copiedTasks = new ArrayList<>();
        for (Task task : tasks) {
            copiedTasks.add(task.copy());
        }
        return new TaskList(copiedTasks);
    }
}
