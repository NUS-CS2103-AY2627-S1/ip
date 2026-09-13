package xuan.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manages a list of tasks and provides operations on the task list.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a TaskList containing the given tasks.
     *
     * @param tasks the initial list of tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index the index of the task
     * @return the task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index the index of the task to delete
     * @return the deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds all deadlines that occur on the specified date.
     *
     * @param targetDate the date to search for deadlines
     * @return the list of deadlines that occur on the specified date
     */
    public ArrayList<Deadline> findDeadlinesOnDate(LocalDate targetDate) {
        return tasks.stream()
                .filter(task -> task instanceof Deadline)
                .map(task -> (Deadline) task)
                .filter(deadline -> deadline.getBy().equals(targetDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds all tasks whose descriptions contain the specified keyword.
     *
     * @param keyword the keyword to search for
     * @return the list of tasks whose descriptions contain the keyword
     */
    public ArrayList<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
