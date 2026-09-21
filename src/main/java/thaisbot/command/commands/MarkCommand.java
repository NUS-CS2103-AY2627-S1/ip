package thaisbot.command.commands;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.Task;
import thaisbot.task.TaskList;

/**
 * Command to mark a task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Construct a MarkCommand.
     * @param taskNumber 1-based index of the task to mark
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ThaisBotException("Task number out of range.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        storage.save(tasks);
        ui.showTaskMarkedDone(task);
    }
}
