package peter.command;

import peter.Parser;
import peter.PeterException;
import peter.storage.Storage;
import peter.task.Task;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Deletes a task identified by its one-based task number.
 */
public class DeleteCommand extends Command {
    private final String command;

    /**
     * Creates a command that deletes the task named by a delete command.
     *
     * <p>The command text is kept rather than a task index because validating
     * the task number needs the task count, which is only known when the
     * command runs.
     *
     * @param command complete delete command entered by the user.
     */
    public DeleteCommand(String command) {
        this.command = command;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The task number is validated here rather than during parsing, since
     * the check needs the current task count. A failed save puts the removed
     * task back at its original position.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PeterException {
        int taskIndex = Parser.parseTaskIndex(command, tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        // Both the rollback and the confirmation message use this task, so a
        // null here would mean the list had been storing one all along.
        assert removedTask != null : "a validated task index must yield a task";

        saveOrRollback(tasks, storage, () -> tasks.add(taskIndex, removedTask));
        ui.showRemovedTask(removedTask, tasks.size());
    }
}
