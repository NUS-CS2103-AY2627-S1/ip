package elora.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Contains the task list and the operations that can be performed on it,
 * such as adding, removing, and querying tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given tasks, typically ones
     * loaded from the save file.
     *
     * @param tasks The initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "caller (Elora) should have already validated this index against the current size";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return The task at that index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "caller (Elora) should have already validated this index against the current size";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return The current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the backing list of every task currently held.
     *
     * @return The full task list, by reference (not a copy).
     */
    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword
     * (case-insensitive).
     *
     * @param keyword The search term to match against descriptions.
     * @return A new list containing only the matching tasks, possibly empty.
     */
    public ArrayList<Task> findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the tasks that occur on the given date.
     *
     * @param date The date to filter by.
     * @return A new list containing only the matching tasks, possibly empty.
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOccurringOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sorts the tasks in place by date: tasks with a due date (currently
     * only deadlines) come first in chronological order, followed by
     * every other task (todos, events) in their original relative order.
     */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(Task::getSortDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
