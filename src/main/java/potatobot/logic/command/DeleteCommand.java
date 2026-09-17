package potatobot.logic.command;

import potatobot.exception.PotatoBotException;
import potatobot.logic.CommandResult;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Deletes a selected task from the task list.
 */
public class DeleteCommand extends TaskNumberCommand implements UndoableCommand {
    private Task deletedTask;
    private int deletedTaskIndex = -1;

    /**
     * Creates a command that deletes the task with the specified displayed number.
     *
     * @param taskNumber Displayed number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) throws PotatoBotException {
        deletedTaskIndex = getTaskIndex(tasks);
        deletedTask = tasks.delete(deletedTaskIndex);
        return CommandResult.reply("Task deleted: " + deletedTask + "\nKeep it going!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo(TaskList tasks) throws PotatoBotException {
        assert deletedTaskIndex >= 0 : "The delete command must execute before it is undone";
        assert deletedTask != null : "An executed delete command must retain its deleted task";
        tasks.add(deletedTaskIndex, deletedTask);
    }
}
