package leo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the in-memory list of tasks that Leo is tracking. Wraps the
 * task data in an {@link ArrayList} so that other classes can add,
 * remove, and look up tasks without working with a raw array (or an
 * artificial capacity limit) directly.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list pre-populated from an existing array of tasks,
     * e.g. as loaded by {@link Storage}.
     *
     * @param initialTasks array containing the initial tasks
     * @param count number of valid entries at the start of initialTasks
     */
    public TaskList(Task[] initialTasks, int count) {
        // A-Assertions: count is meant to say how many of the leading
        // entries in initialTasks are valid (Storage.loadTasks documents
        // the same contract for its own "tasks" parameter). A negative
        // count or one bigger than the array can only happen if a
        // caller passes mismatched arguments - not something a user
        // could trigger through normal use - so this is an internal
        // assumption worth documenting rather than a case to recover
        // from at runtime.
        assert count >= 0 && count <= initialTasks.length
                : "count should be a valid number of leading entries in initialTasks";

        this.tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            this.tasks.add(initialTasks[i]);
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index of the task
     * @return the task at that index
     */
    public Task get(int index) {
        // A-Assertions: every call site in Leo checks isValidIndex(index)
        // before calling get() (see Leo.processCommand), so reaching
        // here with an invalid index would mean a bug in that calling
        // code, not something a user's input could cause directly. This
        // documents that assumption instead of leaving it implicit in
        // however ArrayList happens to fail (an unchecked
        // IndexOutOfBoundsException either way, but this names the
        // actual assumption being violated).
        assert isValidIndex(index) : "index should already have been validated by the caller";

        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the task that was removed
     */
    public Task remove(int index) {
        // A-Assertions: same reasoning as get() above - callers are
        // expected to have validated index first.
        assert isValidIndex(index) : "index should already have been validated by the caller";

        return tasks.remove(index);
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
     * Checks whether the given zero-based index refers to an existing
     * task in the list.
     *
     * @param index index to check
     * @return true if the index is within range, false otherwise
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the tasks as an array, in order, e.g. for saving via
     * {@link Storage}.
     *
     * @return a new array containing the current tasks
     */
    public Task[] toArray() {
        return tasks.toArray(new Task[0]);
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * in their original order. Matching is a plain case-sensitive
     * substring check.
     *
     * @param keyword text to search for within task descriptions
     * @return a new list of the matching tasks (empty if none match)
     */
    public List<Task> find(String keyword) {
        // A-Streams: filtering-then-collecting into a new list is
        // exactly what Stream's filter/collect was designed for, and
        // reads as "the tasks whose description contains keyword" in
        // one expression, matching this method's own Javadoc almost
        // word for word - a closer fit here than the manual loop it
        // replaces.
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether the list already contains a task equivalent to the
     * given one (same type, description, and any type-specific fields),
     * regardless of done status. Used to warn about likely duplicate
     * entries without blocking them outright, since a repeated task is
     * occasionally intentional (e.g. a recurring reminder).
     *
     * @param task task to check for
     * @return true if an equivalent task is already present
     */
    public boolean containsSimilar(Task task) {
        return tasks.stream().anyMatch(existing -> existing.isSameTaskAs(task));
    }
}