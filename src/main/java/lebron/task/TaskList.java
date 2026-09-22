package lebron.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lebron.exception.LebronException;

/**
 * The in-memory list of tasks, with operations to add, delete, and change
 * the done-status of tasks.
 *
 * <p>Task numbers used by the commands are 1-based; this class does the
 * range checking in one place and throws {@link LebronException} (with a
 * ready-to-show message) when a number is out of range, so callers never
 * index the underlying list directly.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a list holding the given tasks (e.g. the ones just loaded). */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @param oneBasedIndex the task number as the user typed it
     * @return the task that was removed
     * @throws LebronException if {@code oneBasedIndex} is not a valid position
     */
    public Task delete(int oneBasedIndex) throws LebronException {
        checkIndex(oneBasedIndex);
        return tasks.remove(oneBasedIndex - 1);
    }

    /**
     * Marks the task at the given 1-based position as done and returns it.
     *
     * @param oneBasedIndex the task number as the user typed it
     * @return the task that was marked
     * @throws LebronException if {@code oneBasedIndex} is not a valid position
     */
    public Task mark(int oneBasedIndex) throws LebronException {
        Task task = get(oneBasedIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given 1-based position as not done and returns it.
     *
     * @param oneBasedIndex the task number as the user typed it
     * @return the task that was unmarked
     * @throws LebronException if {@code oneBasedIndex} is not a valid position
     */
    public Task unmark(int oneBasedIndex) throws LebronException {
        Task task = get(oneBasedIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param oneBasedIndex the task number as the user typed it
     * @return the task at that position
     * @throws LebronException if {@code oneBasedIndex} is not a valid position
     */
    public Task get(int oneBasedIndex) throws LebronException {
        checkIndex(oneBasedIndex);
        // checkIndex above either threw or guarantees this range; asserted
        // so a future edit that weakens checkIndex fails loudly here rather
        // than throwing a confusing IndexOutOfBoundsException below.
        assert oneBasedIndex >= 1 && oneBasedIndex <= tasks.size()
                : "checkIndex should have rejected index " + oneBasedIndex + " already";
        return tasks.get(oneBasedIndex - 1);
    }

    /** Returns an unmodifiable view of the tasks, in order, for reading. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns whether an existing task already has the same type, description
     * and date(s) as {@code candidate} (done-status ignored). Used to warn
     * about a likely-accidental duplicate without blocking it outright --
     * the caller decides what, if anything, to do with the answer.
     *
     * @param candidate the task about to be added
     * @return true if a task with the same details is already in the list
     */
    public boolean hasDuplicateDetails(Task candidate) {
        return tasks.stream().anyMatch(existing -> sameDetails(existing, candidate));
    }

    private static boolean sameDetails(Task first, Task second) {
        return first.getClass() == second.getClass() && detailsKey(first).equals(detailsKey(second));
    }

    /**
     * Returns {@code task}'s data-file line with the done-flag field
     * replaced by a placeholder, so two tasks that differ only in
     * done-status compare equal -- i.e. a key of everything but done-status.
     */
    private static String detailsKey(Task task) {
        String[] fields = task.toFileFormat().split("\\|", -1);
        fields[1] = " - ";
        return String.join("|", fields);
    }

    /**
     * Returns the tasks whose description contains {@code keyword}, in list
     * order. Matching is case-sensitive.
     *
     * @param keyword the text to search for
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    private void checkIndex(int oneBasedIndex) throws LebronException {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new LebronException("OOPS!!! There is no task " + oneBasedIndex
                    + " in your list. You have " + tasks.size() + " task(s).");
        }
    }
}
