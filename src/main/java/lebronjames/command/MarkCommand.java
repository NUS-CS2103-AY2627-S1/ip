package lebronjames.command;

import lebronjames.LebronJamesException;
import lebronjames.storage.Storage;
import lebronjames.task.Task;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Marks a task as done, or as not done yet.
 *
 * <p>Marking and unmarking differ only in which method they call and which
 * message they print, so they share one class with a flag rather than being two
 * near-identical ones.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final boolean isDone;

    /**
     * Creates a command that changes the completion status of one task.
     *
     * @param taskNumber Number of the task as shown by the list command.
     * @param isDone Whether the task should be marked as done.
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LebronJamesException {
        Task task = tasks.get(taskNumber);
        if (isDone) {
            task.markAsDone();
            ui.showTaskMarked(task);
        } else {
            task.markAsNotDone();
            ui.showTaskUnmarked(task);
        }
        storage.save(tasks.asList());
    }
}
