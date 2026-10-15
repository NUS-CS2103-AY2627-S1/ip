package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Task;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Marks one task as done or as not done yet.
 *
 * <p>Both directions are one class because they differ only in the flag they set and
 * the sentence they print.
 */
public class MarkCommand extends Command {

    private final int taskNumber;
    private final boolean isDone;

    /**
     * Creates a command that will change the done status of the given task. Callers use
     * {@link #mark(int)} or {@link #unmark(int)}, which say at the call site which way
     * the status is going.
     *
     * @param taskNumber the number the user gave, counting from 1.
     * @param isDone true to mark the task as done, false to mark it as not done yet.
     */
    private MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    /**
     * Returns a command that marks the given task as done.
     *
     * @param taskNumber the number the user gave, counting from 1.
     * @return the new command.
     */
    public static MarkCommand mark(int taskNumber) {
        return new MarkCommand(taskNumber, true);
    }

    /**
     * Returns a command that marks the given task as not done yet.
     *
     * @param taskNumber the number the user gave, counting from 1.
     * @return the new command.
     */
    public static MarkCommand unmark(int taskNumber) {
        return new MarkCommand(taskNumber, false);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EvException {
        Task task = tasks.getByNumber(taskNumber);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showMarked(task, isDone);
        storage.save(tasks.getTasks());
    }
}
