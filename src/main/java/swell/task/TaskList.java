package swell.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import swell.exception.SwellException;

/**
 * Manages the tasks in Swell.
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
     * Creates a task list with existing tasks.
     *
     * @param tasks existing tasks to manage.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list backing storage should not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Only valid task objects should be added";
        tasks.add(task);
    }

    /**
     * Marks a task as done and returns it.
     *
     * @param taskNumber one-based task number.
     * @return task that was marked as done.
     * @throws SwellException if the task number is outside the current list or the task is already done.
     */
    public Task markTask(int taskNumber) throws SwellException {
        Task task = getTask(taskNumber, "mark");
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not done and returns it.
     *
     * @param taskNumber one-based task number.
     * @return task that was marked as not done.
     * @throws SwellException if the task number is outside the current list or the task is already not done.
     */
    public Task unmarkTask(int taskNumber) throws SwellException {
        Task task = getTask(taskNumber, "unmark");
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes a task and returns it.
     *
     * @param taskNumber one-based task number.
     * @return task that was deleted.
     * @throws SwellException if the task number is outside the current list.
     */
    public Task deleteTask(int taskNumber) throws SwellException {
        getTask(taskNumber, "delete");
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for.
     * @return task list containing matching tasks.
     */
    public TaskList findTasks(String keyword) {
        String keywordLowerCase = keyword.toLowerCase();
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keywordLowerCase))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matchingTasks);
    }

    /**
     * Returns tasks that have the given tag.
     *
     * @param tag tag to search for.
     * @return task list containing tasks with the tag.
     */
    public TaskList findTasksByTag(String tag) {
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.hasTag(tag))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the backing task list for storage.
     *
     * @return backing task list.
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    private Task getTask(int taskNumber, String action) throws SwellException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new SwellException(getTaskNotFoundMessage(action));
        }

        assert taskNumber >= 1 && taskNumber <= tasks.size() : "Validated task number should be in range";
        return tasks.get(taskNumber - 1);
    }

    private String getTaskNotFoundMessage(String action) {
        if (tasks.isEmpty()) {
            return "There aren't any tasks to " + action + " yet.";
        }
        return "I only have tasks 1 to " + tasks.size() + " right now.";
    }
}
