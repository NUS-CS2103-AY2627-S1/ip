package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Removes a task from the list: the {@code delete <number>} command.
 */
public class DeleteCommand extends Command {
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
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the named task, saves, and reports what is left.
     * <p>
     * The removed task is shown back to the user, so the count reported is the
     * size after the removal.
     *
     * @param tasks The list to remove from.
     * @param ui Used to word the confirmation.
     * @param storage Where the shortened list is written.
     * @param history Told how to put the removed task back again.
     * @return The confirmation, behind a warning if the save failed.
     * @throws ThomasException If no task carries that number.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history) throws ThomasException {
        Task removedTask = tasks.deleteByNumber(taskNumber);
        // The task itself has to be kept: nothing else holds it once the list
        // has let go, so without it the removal could not be reversed.
        history.pushInsert(taskNumber, removedTask);
        String saveWarning = save(tasks, ui, storage);
        return saveWarning + ui.getRemovedMessage(removedTask, tasks.size());
    }
}
