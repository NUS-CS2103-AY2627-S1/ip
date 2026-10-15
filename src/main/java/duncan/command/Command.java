package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;

/**
 * Represents one user command, already parsed and ready to run. Each kind
 * of command (adding a task, marking one done, exiting, etc.) is its own
 * subclass that knows how to carry out that one action.
 */
public abstract class Command {
    /** Error shown when a task number is not a whole number that refers to a task in the list. */
    public static final String MESSAGE_INVALID_TASK_NUMBER = "HEY! this task number is bad";

    /** Error shown when a deadline is given without its "/by" date. */
    public static final String MESSAGE_MISSING_BY = "HEY! deadlines must have /by <date/time>";

    /** Error shown when an event is given without both its "/from" and "/to" dates. */
    public static final String MESSAGE_MISSING_FROM_TO = "HEY! events must use /from and /to <date/time>";

    /** Error shown when an event's "/to" date is given before its "/from" date. */
    public static final String MESSAGE_TO_BEFORE_FROM = "HEY! /from must come before /to";

    /** Error shown when a task without dates (i.e. a todo) is asked to be rescheduled or snoozed. */
    public static final String MESSAGE_CANNOT_RESCHEDULE = "HEY! only deadlines and events can be rescheduled";

    /**
     * Error shown when a snooze's number of days is not a positive whole number,
     * or would move a date past the latest date that can be stored.
     */
    public static final String MESSAGE_INVALID_DAYS = "HEY! the number of days is bad";

    /** Warning shown after a change that was made to the list but could not be written to disk. */
    private static final String MESSAGE_NOT_SAVED = "WARNING: that change was made but NOT saved: ";

    /**
     * Carries out this command: updates the task list as needed, reports
     * the result through {@code ui}, and persists any change through
     * {@code storage}.
     *
     * @throws DuncanException if the command cannot be carried out, e.g. a
     *         task number that is out of range
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException;

    /** Whether this command should end the program's main loop. */
    public boolean isExit() {
        return false;
    }

    /**
     * Checks that the given index refers to a task in the list.
     *
     * @param tasks The task list the index should refer into.
     * @param taskIndex The 0-based index to check.
     * @throws DuncanException If the index is out of range.
     */
    protected static void checkTaskIndex(TaskList tasks, int taskIndex) throws DuncanException {
        if (!tasks.isValidIndex(taskIndex)) {
            throw new DuncanException(MESSAGE_INVALID_TASK_NUMBER);
        }
    }

    /**
     * Writes the task list to disk. If that fails, the change stays in the list
     * for this session and a warning saying it was not saved, and why, is shown
     * after the command's own message.
     *
     * @param tasks The task list to save.
     * @param ui Where the warning is shown if saving fails.
     * @param storage Where the task list is saved.
     */
    protected static void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.getTasks());
        } catch (DuncanException e) {
            ui.showWarning(MESSAGE_NOT_SAVED + e.getMessage());
        }
    }
}
