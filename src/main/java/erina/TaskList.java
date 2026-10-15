package erina;

import java.util.ArrayList;
import java.util.List;

import erina.task.Task;

/**
 * The list of tasks the user is keeping.
 *
 * <p>Wraps the underlying {@link List} so that the rest of the program works
 * with tasks in the user's terms, and so that list-wide behaviour added later
 * (searching, sorting) has one natural home.
 */
public class TaskList {
    /** The tasks, in the order the user added them. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, typically loaded from the
     * save file.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "cannot add a null task";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index 0-based position of the task to remove
     * @return the task that was removed
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "index out of range: " + index;
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index 0-based position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "index out of range: " + index;
        return tasks.get(index);
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list holds no tasks.
     *
     * @return {@code true} if there are no tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns whether the list already holds a task with the same details as
     * the given one.
     *
     * @param task the task to look for
     * @return {@code true} if some task in the list is the same task
     * @see Task#isSameTask(Task)
     */
    public boolean contains(Task task) {
        return tasks.stream().anyMatch(existing -> existing.isSameTask(task));
    }

    /**
     * Returns the tasks whose descriptions contain the given keyword.
     *
     * <p>Matching ignores case, so {@code find book} also finds
     * {@code Book club}: when searching, the user is after the task, not the
     * capitalisation they happened to type it with.
     *
     * @param keyword the text to look for inside task descriptions
     * @return the matching tasks, in the order they appear in the list
     */
    public List<Task> find(String keyword) {
        String needle = keyword.toLowerCase();
        // A stream says what is wanted (the tasks that match) rather than how
        // to collect them, and keeps list order because it is sequential.
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .toList();
    }

    /**
     * Returns the tasks as a plain list, for code that needs to iterate or
     * save them.
     *
     * @return the underlying list of tasks
     */
    public List<Task> asList() {
        return tasks;
    }
}
