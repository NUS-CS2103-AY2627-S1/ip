package florkofcows.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import florkofcows.exception.FlorkingExceptions;

/**
 * Mutable list wrapper for Task objects with basic operations.
 *
 * Provides add/mark/unmark/delete operations and simple index parsing/validation
 * used by command implementations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list backed by the given existing list of tasks,
     * typically used when restoring tasks loaded from disk.
     *
     * @param tasks the initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        // The list should only ever contain concrete, usable tasks; nulls would break iteration and persistence.
        assert task != null : "TaskList must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Marks the task at the given 1-based index as done.
     *
     * @param idx the 1-based index of the task to mark.
     * @return the task that was marked.
     * @throws FlorkingExceptions if idx is out of range.
     */
    public Task mark(int idx) throws FlorkingExceptions {
        validateIndex(idx, "mark");
        Task task = tasks.get(idx - 1);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given 1-based index as not done.
     *
     * @param idx the 1-based index of the task to unmark.
     * @return the task that was unmarked.
     * @throws FlorkingExceptions if idx is out of range.
     */
    public Task unmark(int idx) throws FlorkingExceptions {
        validateIndex(idx, "unmark");
        Task task = tasks.get(idx - 1);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the task at the given 1-based index.
     *
     * @param idx the 1-based index of the task to delete.
     * @return the task that was removed.
     * @throws FlorkingExceptions if idx is out of range.
     */
    public Task delete(int idx) throws FlorkingExceptions {
        validateIndex(idx, "delete");
        return tasks.remove(idx - 1);
    }

    /**
     * Adds one or more tags to the task at the given 1-based index.
     *
     * @param idx the 1-based index of the task to tag.
     * @param tags the tags to add.
     * @return the tagged task.
     * @throws FlorkingExceptions if idx is out of range.
     */
    public Task tag(int idx, String... tags) throws FlorkingExceptions {
        validateIndex(idx, "tag");
        Task task = tasks.get(idx - 1);
        task.addTags(tags);
        return task;
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list currently has no tasks.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index the 0-based index.
     * @return the task at that index.
     */
    public Task get(int index) {
        // A valid zero-based index is required for all reads from the backing list.
        assert index >= 0 && index < tasks.size() : "get() requires an in-range index";
        return tasks.get(index);
    }

    /**
     * Returns the underlying list of all tasks, for display or persistence.
     *
     * @return the full list of tasks.
     */
    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Returns all tasks that occur on the given date.
     *
     * @param queryDate the date to filter by.
     * @return a new list containing only the matching tasks.
     */
    public ArrayList<Task> getTasksOn(LocalDate queryDate) {
        return tasks.stream()
                .filter(task -> task.isOccurringOn(queryDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all tasks whose descriptions contain the given keyword.
     *
     * @param keyword the substring to search for in task descriptions.
     * @return a new list containing only the matching tasks.
     */
    public ArrayList<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Parses a task-index argument (e.g. the "3" in "mark 3") and validates
     * it is within range for this list.
     *
     * @param argument the raw argument string.
     * @param actionName the action being performed, used to phrase error messages.
     * @return the parsed, validated 1-based index.
     * @throws FlorkingExceptions if the argument is missing, non-numeric, or out of range.
     */
    public int parseIndex(String argument, String actionName) throws FlorkingExceptions {
        if (argument == null || argument.trim().isEmpty()) {
            throw new FlorkingExceptions("Say properly which task you want " + actionName + ".");
        }
        int idx;
        try {
            idx = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            throw new FlorkingExceptions("Oi, '" + argument.trim() + "' isn't a valid task number eh.");
        }
        validateIndex(idx, actionName);
        // Once validation succeeds, the index must be both positive and within the current task count.
        assert idx >= 1 && idx <= tasks.size() : "Validated task indices must remain in range";
        return idx;
    }

    private void validateIndex(int idx, String actionName) throws FlorkingExceptions {
        if (idx < 1 || idx > tasks.size()) {
            throw new FlorkingExceptions(
                    "You don't have task " + idx + " eh. You only got " + tasks.size() + " task(s).");
        }
    }
}
