package percy;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the list of tasks in memory and provides the operations Percy needs
 * on it: adding, removing and retrieving tasks.
 *
 * <p>This is a thin wrapper around an {@link ArrayList}. Wrapping it (rather
 * than passing the raw list around) keeps list-handling code in one place and
 * lets the rest of the program depend on a small, purpose-built API.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list pre-populated with the given tasks, e.g. the ones
     * loaded from disk by {@link Storage}.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "seed task list should not be null";
        this.tasks = tasks;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        assert isValidIndex(index) : "get called with out-of-range index: " + index;
        return tasks.get(index);
    }

    /** Appends a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "cannot add a null task";
        tasks.add(task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        assert isValidIndex(index) : "remove called with out-of-range index: " + index;
        return tasks.remove(index);
    }

    /** Returns a sequential {@link Stream} over the tasks, in list order. */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Returns true if {@code index} is a valid zero-based position in the list.
     * Used to validate task numbers typed by the user.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns a new list containing only the tasks whose description contains
     * the given keyword, matched case-insensitively and keeping the original
     * order.
     *
     * @param keyword the text to search for
     * @return a task list of the matches (empty if none match)
     */
    public TaskList find(String keyword) {
        assert keyword != null : "search keyword should not be null";
        String lowerKeyword = keyword.toLowerCase();
        ArrayList<Task> matches = stream()
                .filter(task -> task.getRawDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }
}
