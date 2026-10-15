package ralph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the collection of tasks and basic operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList initialised with the provided tasks.
     *
     * @param initial initial collection of tasks; null results in an empty list
     */
    public TaskList(List<Task> initial) {
        this.tasks = new ArrayList<>(initial == null ? Collections.emptyList() : initial);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the list.
     *
     * @param t the task to add
     */
    public void add(Task t) {
        assert t != null : "Task to add must not be null";
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Index to remove is out of bounds";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index index of the requested task
     * @return the task at index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Index to get is out of bounds";
        return tasks.get(index);
    }

    /**
     * Returns an unmodifiable view of all tasks in the list.
     *
     * @return unmodifiable list of tasks
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns an in-memory sorted view of the task list according to the given criterion.
     * Supported criteria: "deadline", "status". The original task ordering is preserved
     * for equal keys (stable sort). The underlying stored list is not modified.
     *
     * @param criterion the sort key, case-insensitive
     * @return a new list containing tasks in the requested order
     * @throws ralph.exception.RalphException if the criterion is not supported
     */
    public List<Task> getAllSorted(String criterion) throws ralph.exception.RalphException {
        String key = criterion == null ? "" : criterion.trim().toLowerCase();
        List<Task> copy = new ArrayList<>(tasks);
        switch (key) {
            case "":
                return Collections.unmodifiableList(copy);
            case "deadline": {
                // Tasks with a date/time (Deadline/Event) come first ordered earliest->latest;
                // tasks without date/time follow. Use a stable sort so original relative order
                // is preserved for equal timestamps and for undated tasks.
                java.util.Comparator<Task> cmp = (a, b) -> {
                    java.time.LocalDateTime ta = extractDateTime(a);
                    java.time.LocalDateTime tb = extractDateTime(b);
                    if (ta == null && tb == null) {
                        return 0;
                    }
                    if (ta == null) {
                        return 1; // a after b
                    }
                    if (tb == null) {
                        return -1; // a before b
                    }
                    return ta.compareTo(tb);
                };
                copy.sort(cmp); // Java sort is stable
                return Collections.unmodifiableList(copy);
            }
            case "status": {
                // incomplete (isDone==false) first, completed after; preserve relative order within groups
                java.util.Comparator<Task> cmp = java.util.Comparator.comparing(Task::isDone);
                copy.sort(cmp);
                return Collections.unmodifiableList(copy);
            }
            default:
                throw new ralph.exception.RalphException("I can sort by deadline or status only.");
        }
    }

    /**
     * Helper to extract the primary date/time from a task: Deadline -> getBy(), Event -> getFrom(), else null.
     */
    private static java.time.LocalDateTime extractDateTime(Task t) {
        if (t instanceof Deadline) {
            return ((Deadline) t).getBy();
        }
        if (t instanceof Event) {
            return ((Event) t).getFrom();
        }
        return null;
    }

    /**
     * Finds tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param keyword the keyword to search for
     * @return matching tasks in list order
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword
            .trim()
            .isEmpty()) {
            return Collections.emptyList();
        }
        String key = keyword
            .trim()
            .toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task
                .getDescription()
                .toLowerCase()
                .contains(key)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
