package yuki.command;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Marks a selected task as completed.
 */
public class MarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that marks one task.
     *
     * @param taskNumber One-based task number to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks, displays, and saves the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.markTask(taskNumber);
        assert task.isDone() : "A task returned by markTask must be marked as done";

        try {
            storage.saveTasks(tasks.getTasks());
        } catch (YukiException e) {
            task.markAsNotDone();
            throw e;
        }
        ui.showTaskStatusChanged("It's done now... I think.", task);
    }
}
