package baymax.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import baymax.exception.BaymaxException;

/**
 * Stores and manages Baymax's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the provided task collection.
     *
     * @param tasks the tasks to manage
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null && !tasks.contains(null)
                : "TaskList should be backed by a collection of non-null tasks.";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "TaskList should only contain real tasks.";
        if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
            throw new BaymaxException(" Sorry, this task is already in your care plan.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task retrieval should use a valid zero-based index.";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task removal should use a valid zero-based index.";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain every keyword in the search query.
     *
     * <p>Each keyword is matched as a case-insensitive substring, allowing users
     * to find tasks with partial words without remembering their capitalization.</p>
     *
     * @param query the space-separated keywords to search for
     * @return a task list containing matching tasks in their original order
     */
    public TaskList find(String query) {
        assert query != null && !query.isBlank()
                : "Task search should receive a validated query.";

        String[] keywords = query.toLowerCase(Locale.ROOT).split("\\s+");
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> {
                    String description = task.getDescription().toLowerCase(Locale.ROOT);
                    return Arrays.stream(keywords).allMatch(description::contains);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        return new TaskList(matchingTasks);
    }
}
