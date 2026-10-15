package lumine.command;

import lumine.task.Task;
import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will mark the task at the given 1-based position as not done.
     *
     * @param taskNumber 1-based index of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * {@inheritDoc}
     * Marks the task as incomplete and prints a confirmation message.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        Task task = taskList.markAsUndone(taskNumber);
        ui.showMessages("OK, I've marked this task as not done yet:", task.toString());
    }
}
