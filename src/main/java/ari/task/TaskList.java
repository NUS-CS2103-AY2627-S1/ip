package ari.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ari.exception.TaskNotFoundException;

/**
 * Stores and manages the user's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Copies the list and every mutable task so staged edits cannot affect this list.
     *
     * @return Independent task list preserving order, types and completion states.
     */
    public TaskList copy() {
        TaskList copy = new TaskList();
        this.tasks.forEach(task -> copy.addTask(task.copy()));
        return copy;
    }

    /**
     * Adds the specified task to the task list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Sorts tasks alphabetically by description, ignoring case.
     * Equal descriptions retain their relative order; displayed IDs follow the new order.
     */
    public void sortByDescription() {
        this.tasks.sort(Comparator.comparing(task -> task.description.toLowerCase(Locale.ROOT)));
    }

    /**
     * Marks the task with the specified task ID as completed.
     *
     * @param taskId One-based ID of the task to mark.
     * @return User-readable representation of the marked task.
     * @throws TaskNotFoundException If the task ID is not in the list.
     */
    public String markTask(int taskId) throws TaskNotFoundException {
        Task task = getTaskById(taskId);
        task.markTask();
        return task.toString();
    }

    /**
     * Marks the task with the specified task ID as incomplete.
     *
     * @param taskId One-based ID of the task to unmark.
     * @return User-readable representation of the unmarked task.
     * @throws TaskNotFoundException If the task ID is not in the list.
     */
    public String unmarkTask(int taskId) throws TaskNotFoundException {
        Task task = getTaskById(taskId);
        task.unmarkTask();
        return task.toString();
    }

    /**
     * Removes the task with the specified task ID.
     *
     * @param taskId One-based ID of the task to delete.
     * @return User-readable representation of the deleted task, or {@code None} if the list is empty.
     * @throws TaskNotFoundException If the list is non-empty and the task ID is not in it.
     */
    public String deleteTask(int taskId) throws TaskNotFoundException {
        if (this.tasks.isEmpty()) {
            return "None";
        }
        Task deletedTask = getTaskById(taskId);
        this.tasks.remove(taskId - 1);

        return deletedTask.toString();
    }

    /**
     * Validates a user-facing ID and returns its task.
     *
     * @param taskId One-based ID supplied by the user.
     * @return Task corresponding to the ID.
     * @throws TaskNotFoundException If the ID is outside the current list.
     */
    private Task getTaskById(int taskId) throws TaskNotFoundException {
        if (taskId <= 0 || taskId > this.tasks.size()) {
            throw new TaskNotFoundException(taskId);
        }
        return this.tasks.get(taskId - 1);
    }

    /**
     * Returns the tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Text to search for in task descriptions.
     * @return New task list containing the matching tasks in their original order.
     */
    public TaskList findMatchingTasks(String keyword) {
        TaskList matchingTasks = new TaskList();
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        this.tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .forEachOrdered(matchingTasks::addTask);

        return matchingTasks;
    }

    /**
     * Returns an unmodifiable copy of all tasks in this list.
     *
     * @return Copy of all tasks in this list.
     */
    public List<Task> getAllTasks() {
        return List.copyOf(this.tasks);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return Task at the specified index.
     */
    public Task getTask(int index) {
        return this.tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks in this list.
     */
    public int getLength() {
        return this.tasks.size();
    }

    /**
     * Returns a message describing the number of tasks in this list.
     *
     * @return Message describing the task count.
     */
    public String getLengthText() {
        String taskNounForm = (this.tasks.size() == 1)
                ? "task"
                : "tasks";

        if (this.tasks.isEmpty()) {
            return "You currently have no tasks remaining. Good job!";
        }

        return String.format(
                "You currently have %s %s in the list! Better get working...",
                this.tasks.size(),
                taskNounForm
        );
    }

    /**
     * Returns the numbered tasks in a user-readable format.
     *
     * @return Numbered task list, or the empty-list message when there are no tasks.
     */
    @Override
    public String toString() {
        if (this.tasks.isEmpty()) {
            return this.getLengthText();
        }

        int taskNumber = 1;
        String taskListText = "";

        for (Task task : this.tasks) {
            taskListText += String.format("%s. %s\n", taskNumber, task);
            taskNumber += 1;
        }

        return taskListText;
    }
}
