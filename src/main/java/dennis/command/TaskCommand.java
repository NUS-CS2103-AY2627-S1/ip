package dennis.command;

import dennis.DennisException;
import dennis.task.TaskList;

/**
 * Shared code for commands that act on one existing task identified by its
 * position in the list: {@code mark}, {@code unmark} and {@code delete}.
 *
 * <p>The position is stored as the user typed it (1-based) and is not
 * range-checked until {@link #checkInRange} is called from {@code execute},
 * because the number of tasks is only known then.</p>
 */
public abstract class TaskCommand extends Command {
    /** 1-based position of the target task, as typed by the user. */
    protected final int taskNumber;

    /**
     * Stores the target task's position for a subclass command.
     *
     * @param taskNumber the target task's position (1 = first task)
     */
    protected TaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Checks that {@link #taskNumber} refers to a task that exists.
     *
     * @param tasks the current task list
     * @throws DennisException if the number is below 1 or past the last task
     */
    protected void checkInRange(TaskList tasks) throws DennisException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new DennisException("Index is out of range!!");
        }
    }
}
