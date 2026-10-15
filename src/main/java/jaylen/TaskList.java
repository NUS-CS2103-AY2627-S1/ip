package jaylen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Owns Jaylen's tasks and the operations that change the task list. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "initial task list should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "task to add should not be null";
        tasks.add(task);
    }

    /**
     * Restores a task at a one-based position after a failed persistent update.
     *
     * @param oneBasedPosition the position at which to restore the task
     * @param task the task to restore
     */
    public void add(int oneBasedPosition, Task task) {
        assert task != null : "task to restore should not be null";
        if (oneBasedPosition < 1 || oneBasedPosition > tasks.size() + 1) {
            throw new JaylenException("That task number does not exist.");
        }
        tasks.add(oneBasedPosition - 1, task);
    }

    /**
     * Returns the task at a one-based position, or an input error if it is absent.
     *
     * @param oneBasedPosition the task position starting at one
     * @return the task at that position
     * @throws JaylenException if the position is outside the list
     */
    public Task get(int oneBasedPosition) {
        return tasks.get(toIndex(oneBasedPosition));
    }

    /**
     * Removes and returns the task at a one-based position.
     *
     * @param oneBasedPosition the task position starting at one
     * @return the removed task
     * @throws JaylenException if the position is outside the list
     */
    public Task remove(int oneBasedPosition) {
        return tasks.remove(toIndex(oneBasedPosition));
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view for display and persistence.
     *
     * @return an unmodifiable snapshot of the tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns one-based positions of tasks whose descriptions contain every search term.
     *
     * <p>Terms are matched case-insensitively as partial text and may appear in any order.
     *
     * @param query the space-separated search terms
     * @return the matching one-based task positions
     */
    public List<Integer> find(String query) {
        List<String> normalizedTerms = List.of(query.toLowerCase(Locale.ROOT).split("\\s+"));
        return IntStream.range(0, tasks.size())
                .filter(index -> matchesAllTerms(tasks.get(index), normalizedTerms))
                .map(index -> index + 1)
                .boxed()
                .toList();
    }

    private boolean matchesAllTerms(Task task, List<String> normalizedTerms) {
        String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
        return normalizedTerms.stream().allMatch(normalizedDescription::contains);
    }

    private int toIndex(int oneBasedPosition) {
        if (oneBasedPosition < 1 || oneBasedPosition > tasks.size()) {
            throw new JaylenException("That task number does not exist.");
        }
        return oneBasedPosition - 1;
    }
}
