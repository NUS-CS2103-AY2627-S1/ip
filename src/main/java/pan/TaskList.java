package pan;

import java.util.ArrayList;

/**
 * The in-memory list of tasks, with the operations the chatbot performs on it
 * (add, delete, look up, count).
 *
 * <p>This is a thin wrapper around an {@link ArrayList}. Wrapping it - rather
 * than passing a raw {@code ArrayList<Task>} around - keeps "what you can do to
 * the task list" in one place and lets the rest of the code depend on this small
 * interface instead of all of {@code ArrayList}.
 */
public class TaskList {

    /** The backing store. Never reassigned, so it is {@code final}. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list (used when there is no save file yet). */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list populated with already-loaded tasks.
     *
     * @param tasks tasks read from the save file by {@link Storage#load()}.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     *
     * @param index position of the task to remove (already range-checked by the caller).
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already be range-checked by the caller, see Pan.parseTaskNumber";
        return tasks.remove(index);
    }

    /** Returns the task at the given 0-based index. */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already be range-checked by the caller, see Pan.parseTaskNumber";
        return tasks.get(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns {@code true} if the list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a new list containing only the tasks whose description contains
     * the given keyword (case-sensitive substring match). The original list is
     * left unchanged.
     *
     * @param keyword text to search for within each task's description.
     */
    public TaskList find(String keyword) {
        assert keyword != null : "keyword should never be null, Parser.arguments() returns \"\" at worst";
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matches.add(task);
            }
        }
        return new TaskList(matches);
    }

    /**
     * Returns the backing list so {@link Storage#save(ArrayList)} can write it
     * out. The list is shared, not copied, so callers should only read from it.
     */
    public ArrayList<Task> asList() {
        return tasks;
    }
}
