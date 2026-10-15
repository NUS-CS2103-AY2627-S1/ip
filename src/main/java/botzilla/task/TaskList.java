package botzilla.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Wraps a list of tasks and provides operations to add, remove, and
 * query them, including finding deadlines/events on a specific date.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList wrapping an existing list of tasks,
     * typically loaded from disk.
     *
     * @param tasks the initial list of tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already have been validated (e.g. by Parser.parseTaskNumber) before reaching "
                + "TaskList; an out-of-range index here indicates a bug in the caller, not bad user input";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index of the task
     * @return the task at that index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already have been validated (e.g. by Parser.parseTaskNumber) before reaching "
                + "TaskList; an out-of-range index here indicates a bug in the caller, not bad user input";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of all tasks.
     *
     * @return the full task list
     */
    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Finds all deadlines and events whose date falls on the given day.
     * Tasks with unparseable dates (stored as raw text) are excluded,
     * since they have no date to compare against.
     *
     * @param date the target calendar date
     * @return a list of matching tasks, in their original order
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        assert date != null : "date should never be null; Parser.parseOnDate always returns a parsed date "
                + "or throws BotzillaException before this is called";
        return tasks.stream()
                .filter(task -> date.equals(dateOf(task)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the calendar date a task falls on, for deadline/event
     * tasks with a successfully parsed date. Returns null for todos and
     * for tasks whose date couldn't be parsed (stored as raw text).
     *
     * @param task the task to extract a date from
     * @return the task's date, or null if it has none
     */
    private static LocalDate dateOf(Task task) {
        return switch (task) {
            case DeadlineTask d -> d.getBy() != null ? d.getBy().toLocalDate() : null;
            case EventTask e -> e.getStart() != null ? e.getStart().toLocalDate() : null;
            default -> null;
        };
    }

    /**
     * Checks whether a task with the same substantive content (type, name,
     * and for deadlines/events its date field(s)) as {@code candidate}
     * already exists in the list, ignoring done/tag state. Used to catch
     * an accidental re-entry of a task that's already on the list.
     *
     * @param candidate the task about to be added
     * @return true if an equivalent task is already present
     */
    public boolean hasDuplicate(Task candidate) {
        return tasks.stream().anyMatch(t -> t.duplicateSignature().equals(candidate.duplicateSignature()));
    }

    /**
     * Returns all tasks whose name contains the given keyword,
     * case-insensitively, in their original order.
     *
     * @param keyword Search term to match against each task's name.
     */
    public ArrayList<Task> findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
