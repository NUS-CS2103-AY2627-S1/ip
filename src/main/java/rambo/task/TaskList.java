package rambo.task;

import java.util.ArrayList;
import java.util.List;

import rambo.exception.RamboException;

/**
 * Contains the application's tasks and provides operations for modifying them.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks in the list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task list should not be null";
        assert tasks.stream().allMatch(task -> task != null)
                : "The initial task list should not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Only valid task objects should be added";
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Adding a task should increase the list size by one";
    }

    /**
     * Toggles the done status of a task.
     *
     * @param taskNumber one-based task number shown to the user
     * @throws RamboException if the task number does not exist
     */
    public void toggle(int taskNumber) throws RamboException {
        Task task = getTask(taskNumber);
        boolean wasDone = task.isDone();
        task.toggleDone();
        assert task.isDone() != wasDone : "Toggling a task should invert its done status";
    }

    /**
     * Assigns a priority level to a task.
     *
     * @param taskNumber one-based task number shown to the user
     * @param priorityLevel priority level from 1 (highest) to 3 (lowest)
     * @throws RamboException if the task number or priority level is invalid
     */
    public void setPriority(int taskNumber, int priorityLevel) throws RamboException {
        Task task = getTask(taskNumber);
        task.setPriorityLevel(priorityLevel);
        assert task.getPriorityLevel() == priorityLevel : "The selected task should receive the requested priority";
    }

    /**
     * Deletes a task from the list.
     *
     * @param taskNumber one-based task number shown to the user
     * @throws RamboException if the task number does not exist
     */
    public void delete(int taskNumber) throws RamboException {
        int previousSize = tasks.size();
        tasks.remove(getIndex(taskNumber));
        assert tasks.size() == previousSize - 1 : "Deleting a task should reduce the list size by one";
    }

    /**
     * Returns a snapshot of the tasks in this list.
     *
     * @return tasks in their current order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the task identified by its one-based display number.
     *
     * @param taskNumber one-based task number shown to the user
     * @return the selected task
     * @throws RamboException if the task number does not exist
     */
    public Task getTask(int taskNumber) throws RamboException {
        return tasks.get(getIndex(taskNumber));
    }

    /**
     * Converts a one-based task number to its list index after validating it.
     */
    private int getIndex(int taskNumber) throws RamboException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new RamboException("I cannot find this task! Give a valid index!");
        }
        assert index >= 0 && index < tasks.size() : "A validated task number should map to a valid index";
        return index;
    }
}
