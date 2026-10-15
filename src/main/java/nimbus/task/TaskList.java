package nimbus.task;

import java.util.ArrayList;
import java.util.List;

import nimbus.exception.NimbusException;

/**
 * Represents a list of tasks in the Nimbus application.
 */
public class TaskList {

    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList object.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList object from the provided list of Tasks.
     *
     * @param tasks the list of tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task list cannot be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list of tasks.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) throws NimbusException {
        if (containsDuplicate(task)) {
            throw new NimbusException("Duplicate task detected.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Index of the task in the list.
     * @return The task at the index.
     * @throws NimbusException If there is no task at the specified index.
     */
    public Task getTask(int index) throws NimbusException {
        requireValidIndex(index);
        return tasks.get(index);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index The index of the task.
     * @return The removed task.
     * @throws NimbusException If there is no task at the specified index.
     */
    public Task removeTask(int index) throws NimbusException {
        requireValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at the index as completed.
     *
     * @param index The index of the task.
     * @return The completed task.
     * @throws NimbusException If the task is already completed or if there is no
     *                         task at that index.
     */
    public Task markTask(int index) throws NimbusException {
        requireValidIndex(index);
        Task task = tasks.get(index);
        if (task.isDone()) {
            throw new NimbusException("You have already completed the task!");
        }
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks the task as incomplete and returns it.
     *
     * @param index The index of the task.
     * @return The unmarked task.
     * @throws NimbusException If the task is already incomplete or if there is no
     *                         task at that index.
     */
    public Task unmarkTask(int index) throws NimbusException {
        requireValidIndex(index);
        Task task = tasks.get(index);
        if (!task.isDone()) {
            throw new NimbusException("Your task is already incomplete!");
        }
        task.unmarkAsDone();
        return task;
    }

    /**
     * Returns the size of the task list
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Checks if the provided index is valid for the task list.
     *
     * @param index The index to check.
     * @throws NimbusException If the index is invalid.
     */
    private void requireValidIndex(int index) throws NimbusException {
        if (index < 0 || index >= tasks.size()) {
            throw new NimbusException("Invalid task index.");
        }
    }

    /**
     * Returns a TaskList with a tasks with names matching the keyword
     *
     * @param keyword
     * @return A TaskList with tasks that match the keyword
     */
    public TaskList findTask(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getName().contains(keyword)) {
                matches.add(task);
            }
        }
        return new TaskList(matches);
    }

    private boolean containsDuplicate(Task task) {
        for (Task existingTask : tasks) {
            if (existingTask.equals(task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + "." + tasks.get(i).toString() + "\n");
        }
        return sb.toString();
    }
}
