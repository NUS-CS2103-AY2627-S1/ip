package billy.command;

import billy.BillyException;
import billy.storage.Storage;
import billy.task.Task;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Removes a task from the list.
 */
public class DeleteCommand extends Command {

    /** The task's number as the user sees it, counting from 1. */
    private final int taskNumber;

    /**
     * Creates a command that will remove one task.
     *
     * @param taskNumber the task's number as the user sees it, counting from 1
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the task the user named, says which one went, and writes the list out.
     *
     * <p>A number naming no task is refused by the list before anything is taken
     * out, so the confirmation can never describe a deletion that did not happen.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BillyException {
        // remove returns the task it took out, so it can be shown in the confirmation.
        Task removed = tasks.remove(taskNumber);
        if (tasks.isEmpty()) {
            // Deleting the last task empties the list, which is worth saying
            // outright rather than reporting as a count of zero.
            ui.show("Gone. Vanished. No trace:", "  " + removed,
                    "And that was the last of them. Empty list. Savor it.");
        } else {
            ui.show("Gone. Vanished. No trace:", "  " + removed,
                    Ui.describeNewListSize(tasks.size()));
        }
        save(tasks, ui, storage);
    }
}
