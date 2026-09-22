package oz.service;

import java.time.LocalDate;
import java.util.List;

import oz.exception.OzException;
import oz.storage.Storage;
import oz.task.RecurringEvent;
import oz.task.Task;
import oz.task.TaskList;

/**
 * Provides task queries and persisted task mutations.
 */
public class TaskService {
    /** Error shown when an occurrence date is supplied for an ordinary task. */
    private static final String ON_RECURRING_ONLY_MESSAGE =
            "The /on argument can only be used with recurring tasks.";

    /** Usage message for marking a recurring occurrence. */
    private static final String MARK_RECURRING_USAGE_MESSAGE =
            "Please specify which occurrence to mark. Use: mark <number> /on <date>.";

    /** Usage message for unmarking a recurring occurrence. */
    private static final String UNMARK_RECURRING_USAGE_MESSAGE =
            "Please specify which occurrence to unmark. Use: unmark <number> /on <date>.";

    /** Error template shown when marking an already completed task. */
    private static final String TASK_ALREADY_DONE_MESSAGE_FORMAT =
            "Task %d is already marked as done.";

    /** Error template shown when unmarking an incomplete task. */
    private static final String TASK_ALREADY_NOT_DONE_MESSAGE_FORMAT =
            "Task %d is not marked as done yet.";

    /** Storage used to persist each successful mutation. */
    private final Storage storage;

    /** In-memory task collection. */
    private final TaskList tasks;

    /**
     * Constructs a service backed by the supplied storage and its loaded tasks.
     *
     * @param storage Storage used to load and save tasks.
     */
    public TaskService(Storage storage) {
        assert storage != null : "Task service storage must be non-null";
        this.storage = storage;
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Returns the number of tasks.
     *
     * @return Current task count.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns an immutable snapshot of the tasks in display order.
     *
     * @return Immutable task snapshot.
     */
    public List<Task> getTasks() {
        return this.tasks.getTasks();
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword Keyword to find.
     * @return Matching tasks in their original order.
     */
    public List<Task> findTasksByKeyword(String keyword) {
        return this.tasks.findTasksByKeyword(keyword);
    }

    /**
     * Finds tasks occurring on the supplied date.
     *
     * @param date Date to query.
     * @return Matching tasks in their original order.
     */
    public List<Task> findTasksOn(LocalDate date) {
        return this.tasks.findTasksOn(date);
    }

    /**
     * Adds and persists a task.
     *
     * @param task Task to add.
     * @return Added task.
     * @throws OzException If persistence fails.
     */
    public Task add(Task task) throws OzException {
        this.tasks.add(task);
        saveOrRollback(() -> this.tasks.delete(this.tasks.size() - 1));
        return task;
    }

    /**
     * Deletes and persists a task.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     * @throws OzException If the index is invalid or persistence fails.
     */
    public Task delete(int index) throws OzException {
        Task removedTask = this.tasks.delete(index);
        saveOrRollback(() -> this.tasks.add(index, removedTask));
        return removedTask;
    }

    /**
     * Marks and persists a task or recurring occurrence as completed.
     *
     * @param index Zero-based task index.
     * @param occurrenceDate Occurrence date, or null for an ordinary task.
     * @return Updated task.
     * @throws OzException If the transition is invalid or persistence fails.
     */
    public Task markTask(int index, LocalDate occurrenceDate) throws OzException {
        Task task = this.tasks.get(index);
        if (occurrenceDate != null) {
            return markOccurrence(task, occurrenceDate);
        }
        if (task instanceof RecurringEvent) {
            throw new OzException(MARK_RECURRING_USAGE_MESSAGE);
        }
        if (task.isDone()) {
            throw new OzException(String.format(TASK_ALREADY_DONE_MESSAGE_FORMAT, index + 1));
        }

        this.tasks.markAsDone(index);
        saveOrRollback(() -> this.tasks.markAsNotDone(index));
        return task;
    }

    /**
     * Marks and persists a task or recurring occurrence as incomplete.
     *
     * @param index Zero-based task index.
     * @param occurrenceDate Occurrence date, or null for an ordinary task.
     * @return Updated task.
     * @throws OzException If the transition is invalid or persistence fails.
     */
    public Task unmarkTask(int index, LocalDate occurrenceDate) throws OzException {
        Task task = this.tasks.get(index);
        if (occurrenceDate != null) {
            return unmarkOccurrence(task, occurrenceDate);
        }
        if (task instanceof RecurringEvent) {
            throw new OzException(UNMARK_RECURRING_USAGE_MESSAGE);
        }
        if (!task.isDone()) {
            throw new OzException(String.format(TASK_ALREADY_NOT_DONE_MESSAGE_FORMAT, index + 1));
        }

        this.tasks.markAsNotDone(index);
        saveOrRollback(() -> this.tasks.markAsDone(index));
        return task;
    }

    /**
     * Marks one recurring occurrence and persists the result.
     *
     * @param task Task expected to be recurring.
     * @param occurrenceDate Occurrence date to mark.
     * @return Updated recurring task.
     * @throws OzException If the task or occurrence is invalid, or persistence fails.
     */
    private Task markOccurrence(Task task, LocalDate occurrenceDate) throws OzException {
        if (!(task instanceof RecurringEvent recurringEvent)) {
            throw new OzException(ON_RECURRING_ONLY_MESSAGE);
        }

        recurringEvent.markOccurrence(occurrenceDate);
        saveOrRollback(() -> recurringEvent.unmarkOccurrence(occurrenceDate));
        return recurringEvent;
    }

    /**
     * Unmarks one recurring occurrence and persists the result.
     *
     * @param task Task expected to be recurring.
     * @param occurrenceDate Occurrence date to unmark.
     * @return Updated recurring task.
     * @throws OzException If the task or occurrence is invalid, or persistence fails.
     */
    private Task unmarkOccurrence(Task task, LocalDate occurrenceDate) throws OzException {
        if (!(task instanceof RecurringEvent recurringEvent)) {
            throw new OzException(ON_RECURRING_ONLY_MESSAGE);
        }

        recurringEvent.unmarkOccurrence(occurrenceDate);
        saveOrRollback(() -> recurringEvent.markOccurrence(occurrenceDate));
        return recurringEvent;
    }

    /**
     * Saves the task list and reverses the mutation if saving fails.
     *
     * @param rollbackAction Action that restores the in-memory state.
     * @throws OzException If saving fails.
     */
    private void saveOrRollback(RollbackAction rollbackAction) throws OzException {
        try {
            this.storage.save(this.tasks.getTasks());
        } catch (OzException saveException) {
            try {
                rollbackAction.run();
            } catch (OzException rollbackException) {
                saveException.addSuppressed(rollbackException);
            }
            throw saveException;
        }
    }

    /**
     * Represents an action that restores an in-memory task mutation.
     */
    @FunctionalInterface
    private interface RollbackAction {
        /**
         * Restores the state that existed before a failed save.
         *
         * @throws OzException If the state cannot be restored.
         */
        void run() throws OzException;
    }
}
