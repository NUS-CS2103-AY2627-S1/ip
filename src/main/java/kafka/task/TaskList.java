package kafka.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import kafka.exception.KafkaException;

/**
 * Stores and manages tasks in list order.
 */
public class TaskList {
    private static final String TASK_NOT_FOUND_ERROR =
            "There is no task with that number ooof. Maybe you can check list agn? (*≧ω≦)";
    private static final String NOT_DEADLINE_ERROR =
            "(✿ヘᴥヘ) Only a deadline can be snoozed with /by.";
    private static final String NOT_EVENT_ERROR =
            "(✿ヘᴥヘ) Only an event can be snoozed with /from or /to.";
    private static final String TODO_SNOOZE_ERROR =
            "(⊃｡•́‿•̀｡)⊃ A todo can't be snoozed because it has no date or time to change";
    private static final String EMPTY_EVENT_SNOOZE_ASSERTION =
            "An event snooze must change at least one timestamp";

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns a copy whose tasks can be edited without changing this list or its tasks.
     */
    public TaskList copy() {
        TaskList copiedTasks = new TaskList();
        for (Task task : tasks) {
            copiedTasks.addTask(task.copy());
        }
        return copiedTasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to remember.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task at the user-facing one-based position.
     *
     * @param taskNumber One-based number of the task to delete.
     * @return Task removed from the list.
     * @throws KafkaException If no task has that number.
     */
    public Task deleteTask(int taskNumber) throws KafkaException {
        return tasks.remove(getTaskIndex(taskNumber));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Reports whether the list has no tasks waiting in it.
     *
     * @return {@code true} when the list contains no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an unmodifiable snapshot of list membership, sharing the existing task objects.
     * Use {@link #copy()} when task edits must be independent of this list.
     *
     * @return Unmodifiable list of the current task references in list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Finds tasks whose displayed text contains the supplied keyword.
     * Matching ignores letter case and preserves the tasks' original order.
     *
     * @param keyword Text to search for.
     * @return Immutable list of matching tasks in their original order.
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.display().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Marks the task at the user-facing one-based position as completed.
     *
     * @param taskNumber One-based number of the task to mark.
     * @return Task that was marked.
     * @throws KafkaException If no task has that number.
     */
    public Task markTask(int taskNumber) throws KafkaException {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the task at the user-facing one-based position as incomplete.
     *
     * @param taskNumber One-based number of the task to unmark.
     * @return Task that was unmarked.
     * @throws KafkaException If no task has that number.
     */
    public Task unmarkTask(int taskNumber) throws KafkaException {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Renames the task at the specified one-based position.
     *
     * @param taskNumber One-based task number.
     * @param newName Replacement task name.
     * @return Display snapshots from before and after the rename.
     * @throws KafkaException If no task has that number.
     */
    public RenameResult renameTask(int taskNumber, String newName) throws KafkaException {
        Task task = getTask(taskNumber);
        String oldDisplay = task.display();
        task.rename(newName);
        return new RenameResult(oldDisplay, task.display());
    }

    /**
     * Reschedules the deadline at the specified one-based position.
     *
     * @param taskNumber One-based task number.
     * @param newBy Replacement deadline.
     * @return Display snapshots from before and after rescheduling.
     * @throws KafkaException If the selected task is absent or is not a deadline.
     */
    public SnoozeResult snoozeDeadline(int taskNumber, String newBy)
            throws KafkaException {
        Task task = getTask(taskNumber);
        if (!(task instanceof Deadline deadline)) {
            throw new KafkaException(task instanceof Todo ? TODO_SNOOZE_ERROR : NOT_DEADLINE_ERROR);
        }

        String oldDisplay = deadline.display();
        deadline.reschedule(newBy);
        return new SnoozeResult(oldDisplay, deadline.display());
    }

    /**
     * Reschedules one or both endpoints of the event at the specified position.
     *
     * @param taskNumber One-based task number.
     * @param newFrom Replacement start, if supplied.
     * @param newTo Replacement end, if supplied.
     * @return Display snapshots from before and after rescheduling.
     * @throws KafkaException If the selected task is absent or is not an event.
     */
    public SnoozeResult snoozeEvent(int taskNumber, Optional<String> newFrom,
            Optional<String> newTo) throws KafkaException {
        assert newFrom.isPresent() || newTo.isPresent()
                : EMPTY_EVENT_SNOOZE_ASSERTION;

        Task task = getTask(taskNumber);
        if (!(task instanceof Event event)) {
            throw new KafkaException(task instanceof Todo ? TODO_SNOOZE_ERROR : NOT_EVENT_ERROR);
        }

        String oldDisplay = event.display();
        newFrom.ifPresent(event::rescheduleFrom);
        newTo.ifPresent(event::rescheduleTo);
        return new SnoozeResult(oldDisplay, event.display());
    }

    /**
     * Finds the task at a user-facing one-based position.
     *
     * @param taskNumber One-based number of the requested task.
     * @return Matching task.
     * @throws KafkaException If no task has that number.
     */
    private Task getTask(int taskNumber) throws KafkaException {
        return tasks.get(getTaskIndex(taskNumber));
    }

    /**
     * Validates a user-facing task number and converts it to a zero-based index.
     *
     * @param taskNumber One-based number supplied by the user.
     * @return Matching zero-based list index.
     * @throws KafkaException If no task has that number.
     */
    private int getTaskIndex(int taskNumber) throws KafkaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new KafkaException(TASK_NOT_FOUND_ERROR);
        }
        return taskNumber - 1;
    }

}
