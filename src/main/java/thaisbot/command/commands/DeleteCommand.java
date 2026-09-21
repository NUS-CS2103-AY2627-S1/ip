package thaisbot.command.commands;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.Task;
import thaisbot.task.TaskList;

/**
 * Command to delete a task by its number.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Construct a DeleteCommand.
     * @param taskNumber 1-based task number
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ThaisBotException("Task number out of range.");
        }
        Task removedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks);
        ui.showTaskRemoved(removedTask, tasks.size());
    }
}
