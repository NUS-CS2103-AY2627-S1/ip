package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Marks a task done: the {@code mark <number>} command.
 */
public class MarkCommand extends Command {
    /**
     * The task number the user typed, counting from 1.
     * <p>
     * Held as the number rather than the task itself, because whether any task
     * carries this number cannot be answered until the list is in hand, which
     * is at {@link #execute} rather than when the command is built.
     */
    private final int taskNumber;

    /**
     * Creates the command for one task number.
     *
     * @param taskNumber The number as the user typed it, counting from 1 and not
     *                   yet checked against the list.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the named task done, saves, and says so.
     *
     * @param tasks The list holding the task.
     * @param ui Used to word the confirmation.
     * @param storage Where the changed list is written.
     * @param history Told what the task's completion was before this.
     * @return The confirmation, behind a warning if the save failed.
     * @throws ThomasException If no task carries that number.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history) throws ThomasException {
        Task task = tasks.getByNumber(taskNumber);
        // Recorded before the change, not after. Marking a task that is already
        // done changes nothing, and an undo that simply unmarked it would take
        // away a tick this command never put there.
        history.pushSetDone(task, task.isDone());
        task.markAsDone();
        String saveWarning = save(tasks, ui, storage);
        return saveWarning + ui.getMarkedMessage(task);
    }
}
