package yuki.command;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Marks a selected task as not completed.
 */
public class UnmarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that unmarks one task.
     *
     * @param taskNumber One-based task number to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Unmarks, displays, and saves the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.unmarkTask(taskNumber);
        assert !task.isDone() : "A task returned by unmarkTask must be marked as not done";

        try {
            storage.saveTasks(tasks.getTasks());
        } catch (YukiException e) {
            task.markAsDone();
            throw e;
        }
        ui.showTaskStatusChanged("The task is no longer marked as done:", task);
    }
}
