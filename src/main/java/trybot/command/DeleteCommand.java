package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.Task;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Deletes one task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a delete command.
     *
     * @param taskNumber one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the selected task, saves the updated list, and reports the result.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     * @throws TryBotException if the task number is outside the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TryBotException("That task number does not exist. Use list to see your task numbers.");
        }

        Task removedTask = tasks.remove(taskNumber - 1);
        // Removing a validated index must return the task that was displayed to the user.
        assert removedTask != null;
        saveTasks(tasks, ui, storage);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Indicates that deleting a task does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
