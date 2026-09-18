package bobby.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bobby.BobbyException;

/**
 * Stores the task list and provides operations that change or inspect it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list from the given tasks.
     *
     * @param tasks tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "TaskList should be backed by a non-null list.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add.
     * @throws BobbyException if the same task already exists.
     */
    public void add(Task task) throws BobbyException {
        assert task != null : "Cannot add a null task.";
        if (containsDuplicateOf(task)) {
            throw new BobbyException("That task is already in your list.");
        }
        tasks.add(task);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to search for.
     * @return matching tasks in their current list order.
     */
    public ArrayList<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Deletes a task by zero-based index.
     *
     * @param taskIndex index of task to delete.
     * @return deleted task.
     */
    public Task delete(int taskIndex) {
        assert isValidIndex(taskIndex) : "Delete should receive a valid task index.";
        return tasks.remove(taskIndex);
    }

    /**
     * Adds a tag to a task by zero-based index.
     *
     * @param taskIndex index of task to tag.
     * @param tag tag to add.
     * @return tagged task.
     * @throws BobbyException if the tag is already present.
     */
    public Task addTag(int taskIndex, String tag) throws BobbyException {
        assert isValidIndex(taskIndex) : "Tag should receive a valid task index.";
        assert Task.isValidTag(tag) : "Tag should be valid before adding.";

        Task task = tasks.get(taskIndex);
        task.addTag(tag);
        return task;
    }

    /**
     * Marks a task as done by zero-based index.
     *
     * @param taskIndex index of task to mark.
     * @return marked task.
     */
    public Task mark(int taskIndex) {
        assert isValidIndex(taskIndex) : "Mark should receive a valid task index.";
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not done by zero-based index.
     *
     * @param taskIndex index of task to unmark.
     * @return unmarked task.
     */
    public Task unmark(int taskIndex) {
        assert isValidIndex(taskIndex) : "Unmark should receive a valid task index.";
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns whether the given zero-based task index exists.
     *
     * @param taskIndex index to check.
     * @return true if the index points to an existing task.
     */
    public boolean isValidIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size();
    }

    /**
     * Returns the current number of tasks.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the current tasks for display and storage.
     *
     * @return current tasks.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    private boolean containsDuplicateOf(Task task) {
        return tasks.stream()
                .anyMatch(existingTask -> existingTask.getIdentityKey().equals(task.getIdentityKey()));
    }
}
