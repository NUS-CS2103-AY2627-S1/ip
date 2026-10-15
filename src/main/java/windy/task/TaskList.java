package windy.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the application's collection of tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list backed by the supplied list.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns a deep copy of this list and its tasks.
     *
     * @return an independent task list.
     */
    public TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Replaces this list with independent copies of the supplied tasks.
     *
     * @param source the replacement contents.
     */
    public void replaceWith(TaskList source) {
        TaskList replacement = source.copy();
        tasks.clear();
        tasks.addAll(replacement.tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Returns an unmodifiable snapshot of the current tasks.
     *
     * @return a copy of the task list.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param taskIndex the zero-based index of the task.
     * @return the removed task.
     */
    public Task deleteTask(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int getNumTasks() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param taskIndex the zero-based index of the task.
     * @return the selected task.
     */
    public Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Marks a selected task as completed.
     *
     * @param taskIndex the zero-based index of the task.
     */
    public void markTaskAsDone(int taskIndex) {
        tasks.get(taskIndex).setDone(true);
    }

    /**
     * Marks a selected task as not completed.
     *
     * @param taskIndex the zero-based index of the task.
     */
    public void markTaskAsNotDone(int taskIndex) {
        tasks.get(taskIndex).setDone(false);
    }

    /**
     * Finds incomplete tasks that occur on the specified date.
     *
     * @param localDate the date to search for.
     * @return tasks that occur on the date.
     */
    public List<Task> findTasksByDate(LocalDate localDate) {
        return tasks.stream()
                .filter(task -> task.occursOnDate(localDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds all tasks whose descriptions contain the specified keyword.
     *
     * @param keyword the keyword to search for.
     * @return tasks with descriptions that contain the keyword.
     */
    public List<Task> findTasksContainingKeyword(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
