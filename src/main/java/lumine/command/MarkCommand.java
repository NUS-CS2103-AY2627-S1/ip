package lumine.command;

import lumine.task.Task;
import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will mark the task at the given 1-based position as done.
     *
     * @param taskNumber 1-based index of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * {@inheritDoc}
     * Marks the task done and prints a confirmation message.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        Task task = taskList.markAsDone(taskNumber);
        ui.showMessages("A step forward! Marked done:", task.toString());
    }
}
