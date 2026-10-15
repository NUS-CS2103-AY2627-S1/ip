package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Marks a task not done again: the {@code unmark <number>} command.
 * <p>
 * Deliberately its own class rather than a {@link MarkCommand} carrying a
 * true/false flag. The two differ only in one call and one message, but a
 * boolean argument at the call site says nothing about which way round it is:
 * {@code new MarkCommand(3, false)} has to be looked up, where
 * {@code new UnmarkCommand(3)} reads as what it does.
 */
public class UnmarkCommand extends Command {
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
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the named task not done, saves, and says so.
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
        // Recorded before the change, for the reason MarkCommand gives: unmarking
        // a task that is already not done changes nothing, so an undo must put the
        // flag back to what it was rather than flip it.
        history.pushSetDone(task, task.isDone());
        task.unmarkAsDone();
        String saveWarning = save(tasks, ui, storage);
        return saveWarning + ui.getUnmarkedMessage(task);
    }
}
