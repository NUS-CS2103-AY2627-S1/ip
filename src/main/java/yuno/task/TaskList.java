package yuno.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import yuno.exception.InvalidTaskNumberException;

/**
 * Stores and manages the tasks currently available to the chatbot.
 */
public class TaskList {
    /** Stores the tasks in the order they were added. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns a separate task list containing the same task objects in the same order.
     * Structural changes to the copy do not affect this list.
     *
     * @return Copy containing the same tasks and ordering.
     */
    public TaskList copy() {
        TaskList copiedTaskList = new TaskList();
        copiedTaskList.tasks.addAll(tasks);
        return copiedTaskList;
    }

    /**
     * Returns an independent copy of this task list and every task it contains.
     *
     * @return Copy containing independent tasks with the same data and ordering.
     */
    public TaskList deepCopy() {
        TaskList copiedTaskList = new TaskList();
        tasks.stream().map(Task::copy).forEach(copiedTaskList::addTask);
        return copiedTaskList;
    }

    /**
     * Replaces this task list's contents with the contents of another task list.
     *
     * @param replacementTaskList Task list whose contents should be adopted.
     */
    public void replaceWith(TaskList replacementTaskList) {
        assert replacementTaskList != null : "Replacement task list must not be null";
        assert replacementTaskList != this : "Task list cannot replace itself";
        tasks.clear();
        tasks.addAll(replacementTaskList.tasks);
    }

    /**
     * Returns the number of tasks in this task list.
     *
     * @return Number of stored tasks.
     */
    public int getCount() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified one-based position.
     *
     * @param taskNumber One-based position of the task.
     * @return Task at the specified position.
     * @throws InvalidTaskNumberException If the position is outside the task list.
     */
    public Task getTask(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > getCount()) {
            throw new InvalidTaskNumberException(
                    "Are you wasting my time? The integer you gave is out of bounds.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Adds a new incomplete task with the specified description.
     *
     * @param description Description of the task to add.
     * @return Added incomplete to-do task.
     */
    public Todo addTask(String description) {
        Todo addedTask = new Todo(description, false);
        tasks.add(addedTask);
        return addedTask;
    }

    /**
     * Adds a new incomplete deadline task with the specified description and deadline.
     *
     * @param description Description of the task to add.
     * @param deadlineDateTime Date and time by which the task must be completed.
     * @return Added incomplete deadline task.
     */
    public Deadline addTask(String description, LocalDateTime deadlineDateTime) {
        Deadline addedTask = new Deadline(description, false, deadlineDateTime);
        tasks.add(addedTask);
        return addedTask;
    }

    /**
     * Adds a new incomplete event task with the specified description and times.
     *
     * @param description Description of the task to add.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     * @return Added incomplete event task.
     */
    public Event addTask(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Event addedTask = new Event(description, false, startDateTime, endDateTime);
        tasks.add(addedTask);
        return addedTask;
    }

    /**
     * Adds the specified existing task while preserving its task type and completion status.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task list cannot contain null tasks";
        tasks.add(task);
    }

    /**
     * Marks the task at the specified one-based position as completed.
     *
     * @param taskNumber One-based position of the task to mark.
     * @throws InvalidTaskNumberException If the position is outside the task list.
     */
    public void markTask(int taskNumber) throws InvalidTaskNumberException {
        getTask(taskNumber).markDone();
    }

    /**
     * Marks the task at the specified one-based position as incomplete.
     *
     * @param taskNumber One-based position of the task to unmark.
     * @throws InvalidTaskNumberException If the position is outside the task list.
     */
    public void unmarkTask(int taskNumber) throws InvalidTaskNumberException {
        getTask(taskNumber).unmarkDone();
    }

    /**
     * Deletes and returns the task at the specified one-based position.
     *
     * @param taskNumber One-based position of the task to delete.
     * @return Deleted task.
     * @throws InvalidTaskNumberException If the position is outside the task list.
     */
    public Task deleteTask(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > getCount()) {
            throw new InvalidTaskNumberException(
                    "What do you want me to delete, your brain? The integer you gave is out of bounds.");
        }
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Removes every task from this task list.
     */
    public void clearTasks() {
        tasks.clear();
    }

    /**
     * Returns the tasks that are relevant for the specified date.
     *
     * @param date Date for which tasks are requested.
     * @return Tasks relevant for the date, in their original list order.
     */
    public List<Task> findTasksFor(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isRelevantFor(date))
                .toList();
    }

    /**
     * Returns the tasks whose descriptions contain the specified word.
     *
     * @param searchText Text to find within each task description.
     * @return Tasks with matching descriptions, in their original list order.
     */
    public List<Task> findTasksFor(String searchText) {
        return tasks.stream()
                .filter(task -> task.containsText(searchText))
                .toList();
    }

    /**
     * Permanently sorts dated tasks by time while retaining undated tasks at the end.
     * Tasks with equal chronological times retain their relative order.
     *
     * @param isAscending Whether dated tasks should be ordered from earliest to latest.
     */
    public void sortChronologically(boolean isAscending) {
        Comparator<Task> dateTimeComparator = Comparator.comparing(Task::getChronologicalTime);
        if (!isAscending) {
            dateTimeComparator = dateTimeComparator.reversed();
        }

        Comparator<Task> chronologicalComparator = Comparator
                .comparing((Task task) -> task instanceof Todo)
                .thenComparing(dateTimeComparator);

        tasks.sort(chronologicalComparator);
    }
}
