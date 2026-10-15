package olaf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stores Olaf's tasks in insertion order.
 */
final class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    TaskList() {
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage.
     */
    TaskList(List<Task> initialTasks) {
        tasks.addAll(initialTasks);
    }

    /** Returns a snapshot whose tasks can be edited without changing this list's tasks. */
    TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy).toList());
    }

    /**
     * Adds the supplied task to the end of the list.
     *
     * @param task task to add.
     */
    void add(Task task) {
        assert task != null : "Only non-null tasks can be added";
        tasks.add(task);
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions
     * @return immutable list of matches with their original task numbers in insertion order
     */
    List<TaskMatch> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<TaskMatch> matchingTasks = new ArrayList<>();

        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTasks.add(new TaskMatch(index + 1, task));
            }
        }
        return List.copyOf(matchingTasks);
    }

    /**
     * Marks the task identified by its user-facing number as done.
     *
     * @param taskNumber one-based task number shown by the list command.
     * @return the task that was marked
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task markAsDone(int taskNumber) throws InvalidTaskNumberException {
        Task task = getByTaskNumber(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task identified by its user-facing number as not done.
     *
     * @param taskNumber one-based task number shown by the list command.
     * @return the task that was marked as not done
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task markAsNotDone(int taskNumber) throws InvalidTaskNumberException {
        Task task = getByTaskNumber(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes the task identified by its user-facing number.
     *
     * @param taskNumber one-based task number shown by the list command.
     * @return the deleted task
     * @throws InvalidTaskNumberException if no task has the supplied number
     */
    Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Replaces the due date of the selected deadline without changing its list position or status.
     *
     * @param taskNumber one-based task number shown by the list command
     * @param dueDate replacement due date
     * @return the updated deadline
     * @throws InvalidTaskNumberException if no task has the supplied number
     * @throws TaskRescheduleException if the selected task is not a deadline
     */
    Task rescheduleDeadline(int taskNumber, LocalDate dueDate)
            throws InvalidTaskNumberException, TaskRescheduleException {
        Task task = getByTaskNumber(taskNumber);
        if (!(task instanceof Deadline deadline)) {
            throw createScheduleTypeException(task);
        }
        deadline.reschedule(dueDate);
        return deadline;
    }

    /**
     * Replaces both dates of the selected event without changing its list position or status.
     *
     * @param taskNumber one-based task number shown by the list command
     * @param startDate replacement start date
     * @param endDate replacement end date
     * @return the updated event
     * @throws InvalidTaskNumberException if no task has the supplied number
     * @throws TaskRescheduleException if the task is not an event or the date range is reversed
     */
    Task rescheduleEvent(int taskNumber, LocalDate startDate, LocalDate endDate)
            throws InvalidTaskNumberException, TaskRescheduleException {
        Task task = getByTaskNumber(taskNumber);
        if (!(task instanceof Event event)) {
            throw createScheduleTypeException(task);
        }
        event.reschedule(startDate, endDate);
        return event;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return task count
     */
    int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the task references for display or persistence.
     * Changes to this list do not affect the snapshot, but the task objects remain mutable.
     *
     * @return snapshot of all task references in insertion order
     */
    List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    private TaskRescheduleException createScheduleTypeException(Task task) {
        if (task instanceof Deadline) {
            return new TaskRescheduleException("This task is a deadline. "
                    + "Use 'reschedule <task number> /by <yyyy-MM-dd>'.");
        }
        if (task instanceof Event) {
            return new TaskRescheduleException("This task is an event. "
                    + "Use 'reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>'.");
        }
        return new TaskRescheduleException("ToDos have no dates to reschedule. "
                + "Use 'list' to choose a deadline or event.");
    }

    private Task getByTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException(tasks.size());
        }
    }
}
