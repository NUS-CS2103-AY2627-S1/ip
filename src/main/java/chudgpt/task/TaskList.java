package chudgpt.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import chudgpt.exception.ChudException;

/** Stores tasks in their display and serialization order. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Source task list should not be null";
        assert tasks.stream().allMatch(task -> task != null)
                : "Source task list should not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to be added
     * @return the task that was added
     */
    public Task addTask(Task task) {
        assert task != null : "Task to add should not be null";
        int previousSize = tasks.size();

        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Adding a task should increase the list size by one";
        return task;
    }

    /**
     * Adds a task unless an existing task has the same type and details.
     *
     * @param task task to add.
     * @return the task that was added.
     * @throws ChudException if the task duplicates an existing task.
     */
    public Task addUniqueTask(Task task) throws ChudException {
        int duplicateIndex = findDuplicateIndex(task);
        if (duplicateIndex >= 0) {
            throw new ChudException("This task duplicates task " + (duplicateIndex + 1) + ".");
        }
        return addTask(task);
    }

    /** Returns the zero-based index of a task with the same details, or {@code -1} if none exists. */
    private int findDuplicateIndex(Task candidate) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).hasSameDetails(candidate))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Removes the specified task from the list.
     *
     * @param index the index of the task to be removed
     * @return the task that was removed
     * @throws ChudException if the index is out of range
     */
    public Task deleteTask(int index) throws ChudException {
        validateIndex(index);

        int previousSize = tasks.size();
        Task deletedTask = tasks.remove(index);
        assert deletedTask != null : "Deleted task should not be null";
        assert tasks.size() == previousSize - 1 : "Deleting a task should decrease the list size by one";

        return deletedTask;
    }

    /**
     * Returns the specified task from the list.
     *
     * @param index the index of the task to be returned
     * @return the selected task
     * @throws ChudException if the index is out of range
     */
    public Task getTask(int index) throws ChudException {
        validateIndex(index);
        return tasks.get(index);
    }

    /**
     * Updates the completion status of the task at the specified index.
     *
     * @param index index of the task to update.
     * @param isCompleted target completion status.
     * @return the updated task.
     * @throws ChudException if the index is out of range.
     */
    public Task updateTask(int index, boolean isCompleted) throws ChudException {
        validateIndex(index);

        Task updatedTask = tasks.get(index).setCompleted(isCompleted);
        assert updatedTask.isCompleted() == isCompleted : "Updated task should have the requested status";
        return updatedTask;
    }

    /**
     * Updates the priority of the selected task.
     *
     * @param index zero-based index of the task to update.
     * @param priority priority to assign.
     * @return the updated task.
     * @throws ChudException if the index is out of range.
     */
    public Task updatePriority(int index, Priority priority) throws ChudException {
        validateIndex(index);
        return tasks.get(index).setPriority(priority);
    }

    /** Ensures that an index identifies a task currently in the list. */
    private void validateIndex(int index) throws ChudException {
        if (index < 0 || index >= tasks.size()) {
            long taskNumber = (long) index + 1;
            throw new ChudException("Task number " + taskNumber + " does not exist.");
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds all tasks whose descriptions have the keyword
     *
     * @param keyword the keyword to search for.
     * @return a new Task List that contains tasks that match the keyword.
     */
    public TaskList findTasks(String keyword) {
        assert keyword != null : "Search keyword should not be null";
        List<Task> matches = tasks.stream().filter(task -> task.description
                .toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT)))
                .toList();
        return new TaskList(matches);
    }


    /**
     * Formats all tasks in the list into a single serialized string suitable for file storage.
     *
     * @return a line-separated string representing the tasks in save format.
     */
    public String toFileFormat() {
        return tasks.stream()
                .map(Task::toSaveMessage)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Renders a stable priority-sorted view using the current underlying task numbers.
     * The backing list and its serialization order are not changed.
     *
     * @return tasks from highest to lowest priority, with ties in underlying order.
     */
    public String toPrioritySortedString() {
        return IntStream.range(0, tasks.size())
                .boxed()
                .sorted(Comparator.comparing(index -> tasks.get(index).getPriority()))
                .map(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
    }
}
