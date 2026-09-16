package grower.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import grower.exceptions.InvalidTaskNumberException;

/**
 * Manages tasks in their display order.
 */
public class TaskList {
    private final List<Task> listOfTasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        listOfTasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        listOfTasks.add(task);
    }

    /**
     * Marks the task at the specified index as completed.
     *
     * @param index Zero-based index of the task to mark.
     * @return Task that was marked.
     * @throws InvalidTaskNumberException If the index is outside the task list.
     */
    public Task markTask(int index) throws InvalidTaskNumberException {
        validateIndex(index);
        Task task = listOfTasks.get(index);
        task.mark();
        return task;
    }

    /**
     * Marks the task at the specified index as not completed.
     *
     * @param index Zero-based index of the task to unmark.
     * @return Task that was unmarked.
     * @throws InvalidTaskNumberException If the index is outside the task list.
     */
    public Task unmarkTask(int index) throws InvalidTaskNumberException {
        validateIndex(index);
        Task task = listOfTasks.get(index);
        task.unmark();
        return task;
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index Zero-based index of the task to delete.
     * @return Task that was deleted.
     * @throws InvalidTaskNumberException If the index is outside the task list.
     */
    public Task deleteTask(int index) throws InvalidTaskNumberException {
        validateIndex(index);
        return listOfTasks.remove(index);
    }

    /**
     * Returns an unmodifiable snapshot of the current task order.
     * The task objects are shared with this list and remain mutable.
     *
     * @return Tasks in their current order.
     */
    public List<Task> getTasks() {
        return List.copyOf(listOfTasks);
    }

    /**
     * Checks that an index refers to an existing task.
     *
     * @param index Zero-based index to check.
     * @throws InvalidTaskNumberException If the index is outside the task list.
     */
    private void validateIndex(int index) throws InvalidTaskNumberException {
        if (index < 0 || index >= listOfTasks.size()) {
            throw new InvalidTaskNumberException(index, listOfTasks.size());
        }
    }

    /**
     * Returns serialized data for all tasks in their current order.
     *
     * @return Serialized task data.
     */
    public List<String> getTaskData() {
        return listOfTasks.stream()
                .map(Task::toFileString)
                .toList();
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword Text to search for in task descriptions.
     * @return Matching tasks in their original display order.
     */
    public List<Task> findTasks(String keyword) {
        return listOfTasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Sorts tasks in place, placing dateless tasks first and then
     * dated tasks from earliest to latest. Equal times retain
     * their existing relative order.
     */
    public void sortByTime() {
        listOfTasks.sort(Comparator.comparing(
                Task::getSortTime,
                Comparator.nullsFirst(Comparator.naturalOrder())));
    }
}
