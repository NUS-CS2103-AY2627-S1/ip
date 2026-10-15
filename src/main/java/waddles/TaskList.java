package waddles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains the task list and operations to add, delete, and retrieve tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList containing the given tasks, e.g. loaded from disk.
     *
     * @param loadedTasks The tasks to populate the list with.
     */
    public TaskList(List<Task> loadedTasks) {
        assert loadedTasks != null : "loadedTasks should never be null; Storage#load always returns "
                + "an empty list rather than null, even when the data file is missing or corrupted";
        tasks = loadedTasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to the list";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-indexed position of the task to remove.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> getAll() {
        return tasks;
    }

    /**
     * Replaces the entire contents of this list with the given tasks, e.g.
     * to restore a previously saved snapshot for the "undo" command.
     *
     * @param newTasks The tasks that should make up the list going forward.
     */
    public void setAll(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * @param keyword The keyword to search for.
     * @return The list of matching tasks, in original order.
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "Search keyword should never be null; "
                + "Parser#parseFind rejects empty input but always returns a non-null keyword";
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Returns the tasks scheduled to occur on the given date, for the
     * "schedule" command. A ToDo never matches, since it has no date; a
     * Deadline matches its own date, and an Event matches any date within
     * its (inclusive) span.
     *
     * @param date The date to view the schedule for.
     * @return The matching tasks, in original order.
     */
    public List<Task> findOnDate(LocalDate date) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
