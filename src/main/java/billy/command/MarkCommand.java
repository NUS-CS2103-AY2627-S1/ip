package billy.command;

import billy.BillyException;
import billy.storage.Storage;
import billy.task.Task;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Marks a task as done, or as not done again.
 *
 * <p>Both are one class because they differ only in the status they set and the
 * words they report. Splitting them would duplicate the rest for the sake of a
 * single boolean.
 */
public class MarkCommand extends Command {

    /** The task's number as the user sees it, counting from 1. */
    private final int taskNumber;

    /** The status to set: true for done, false for not done yet. */
    private final boolean shouldBeDone;

    /**
     * Creates a command that will change one task's status.
     *
     * @param taskNumber the task's number as the user sees it, counting from 1
     * @param shouldBeDone true to mark it done, false to mark it not done again
     */
    public MarkCommand(int taskNumber, boolean shouldBeDone) {
        this.taskNumber = taskNumber;
        this.shouldBeDone = shouldBeDone;
    }

    /**
     * Changes the task's status, says which way it went, and writes the list out.
     *
     * <p>The task is changed in place, so the list holds the new status without
     * anything having to be put back into it.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BillyException {
        Task task = tasks.get(taskNumber);
        if (shouldBeDone) {
            task.markAsDone();
            // Finishing the last outstanding task is the one moment in a session
            // worth making something of, so it gets a line the others do not.
            if (tasks.isAllDone()) {
                ui.show("BOOM. Done and dusted:", "  " + task,
                        "And that's your whole list conquered. Take a bow.");
            } else {
                ui.show("BOOM. Done and dusted:", "  " + task);
            }
        } else {
            task.markAsNotDone();
            ui.show("Un-done! We've all been there:", "  " + task);
        }
        save(tasks, ui, storage);
    }
}
