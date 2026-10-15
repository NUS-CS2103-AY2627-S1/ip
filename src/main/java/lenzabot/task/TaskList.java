package lenzabot.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lenzabot.LenZaBotException;

/**
 * Holds the list of tasks and provides operations for modifying it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list that starts with the given tasks,
     * e.g. loaded from the save file by Storage.
     *
     * @param initialTasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds the given task to the list.
     *
     * @param task Task to append to the list.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given one-based index and returns it.
     *
     * @param index One-based position of the task to remove.
     * @return Removed task.
     * @throws LenZaBotException If the index does not refer to an existing task.
     */
    public Task deleteTask(int index) throws LenZaBotException {
        getTaskByIndex(index);
        return tasks.remove(index - 1);
    }

    /**
     * Marks the task at the given one-based index as completed and returns it.
     *
     * @param index One-based position of the task to mark.
     * @return Task that was marked as completed.
     * @throws LenZaBotException If the index does not refer to an existing task.
     */
    public Task markTask(int index) throws LenZaBotException {
        return setCompletion(index, true);
    }

    /**
     * Marks the task at the given one-based index as incomplete and returns it.
     *
     * @param index One-based position of the task to unmark.
     * @return Task that was marked as incomplete.
     * @throws LenZaBotException If the index does not refer to an existing task.
     */
    public Task unmarkTask(int index) throws LenZaBotException {
        return setCompletion(index, false);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks in the list.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable view of all tasks in insertion order.
     *
     * @return Unmodifiable view of tasks in insertion order.
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword,
     * ignoring letter case.
     *
     * @param keyword Text to search for within task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    // Returns the task associated with the one-based index shown to the
    // user by the `list` command.
    private Task getTaskByIndex(int index) throws LenZaBotException {
        if (tasks.isEmpty()) {
            throw new LenZaBotException("there are no tasks in the list yet.");
        }

        if (index < 1 || index > tasks.size()) {
            throw new LenZaBotException("task number must be between 1 and " + tasks.size() + ".");
        }

        return tasks.get(index - 1);
    }

    // Sets the completion status of the indexed task and returns it.
    private Task setCompletion(int index, boolean isCompleted) throws LenZaBotException {
        Task task = getTaskByIndex(index);
        if (isCompleted) {
            task.markAsCompleted();
        } else {
            task.markAsIncomplete();
        }
        return task;
    }
}
