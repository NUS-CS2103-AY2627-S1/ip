package sal;

import java.util.ArrayList;

/**
 * A mutable list of tasks with operations to add, delete, and look up tasks.
 * Keeps task-management logic out of the main chatbot class.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list that starts with the given tasks (e.g. loaded from disk).
     *
     * @param tasks Initial tasks; the list is copied so callers cannot mutate it directly.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     * @throws SalException If the index is out of range.
     */
    public Task delete(int index) throws SalException {
        checkIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Zero-based index.
     * @return The task at that index.
     * @throws SalException If the index is out of range.
     */
    public Task get(int index) throws SalException {
        checkIndex(index);
        return tasks.get(index);
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index Zero-based index.
     * @return The updated task.
     * @throws SalException If the index is out of range.
     */
    public Task markAsDone(int index) throws SalException {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index.
     * @return The updated task.
     * @throws SalException If the index is out of range.
     */
    public Task markAsNotDone(int index) throws SalException {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Adds a tag to the task at the given index.
     *
     * @param index Zero-based index.
     * @param tagName Tag name without a leading {@code #}.
     * @return The updated task.
     * @throws SalException If the index is out of range or the tag cannot be added.
     */
    public Task tag(int index, String tagName) throws SalException {
        Task task = get(index);
        task.addTag(tagName);
        return task;
    }

    /**
     * Returns tasks whose description contains the given keyword.
     * Matching is a case-sensitive substring search, in list order.
     *
     * @param keyword Text to look for in each task description.
     * @return Matching tasks; empty if none match. The original list is unchanged.
     */
    public ArrayList<Task> find(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.description.contains(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list for persistence.
     * Callers should treat the returned list as read-only for iteration.
     *
     * @return All tasks in order.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Ensures {@code index} is a valid zero-based position in this list.
     */
    private void checkIndex(int index) throws SalException {
        if (index < 0 || index >= tasks.size()) {
            throw new SalException("Task number out of bounds (0-" + tasks.size());
        }
    }
}
