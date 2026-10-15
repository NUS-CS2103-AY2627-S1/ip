package tungtung;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Stores the tasks in the order that they should be shown to the user.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to place in the list
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list must be created from a task collection.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "The task list must not contain null tasks.";
        tasks.add(task);
    }

    /**
     * Inserts a task at a particular zero-based position.
     *
     * @param index zero-based insertion position
     * @param task task to insert
     */
    public void add(int index, Task task) {
        assert task != null : "The task list must not contain null tasks.";
        assert index >= 0 && index <= tasks.size() : "Insertion index must be within the list bounds.";
        tasks.add(index, task);
    }

    /**
     * Returns a task at a particular zero-based position.
     *
     * @param index zero-based task position
     * @return task at the requested position
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "A requested task index must refer to an existing task.";
        return tasks.get(index);
    }

    /** Returns the task's zero-based position in the full list, or -1 if absent. */
    public int indexOf(Task task) {
        return tasks.indexOf(task);
    }

    /**
     * Removes and returns a task at a particular zero-based position.
     *
     * @param index zero-based task position
     * @return removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "A removed task index must refer to an existing task.";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the tasks for saving.
     *
     * @return copy of the current tasks
     */
    public ArrayList<Task> toArrayList() {
        return new ArrayList<>(tasks);
    }

    /** Returns tasks whose descriptions contain the supplied keyword, ignoring case. */
    public ArrayList<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Sorts dated tasks chronologically and places undated tasks at the end. */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(this::getTaskDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private LocalDate getTaskDate(Task task) {
        if (task instanceof Deadline) {
            return ((Deadline) task).by;
        }
        if (task instanceof Event) {
            return ((Event) task).from;
        }
        return null;
    }
}
