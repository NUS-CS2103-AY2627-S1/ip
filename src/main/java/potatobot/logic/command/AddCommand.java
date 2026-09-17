package potatobot.logic.command;

import potatobot.exception.PotatoBotException;
import potatobot.logic.CommandResult;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command implements UndoableCommand {
    private final Task task;
    private int addedTaskIndex = -1;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) throws PotatoBotException {
        addedTaskIndex = tasks.size();
        tasks.add(task);
        return CommandResult.reply("Added: " + task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo(TaskList tasks) {
        assert addedTaskIndex >= 0 : "The add command must execute before it is undone";
        Task removedTask = tasks.delete(addedTaskIndex);
        assert removedTask == task : "Undoing an add command must remove the task it added";
    }
}
