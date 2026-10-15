package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "delete" command, which removes a task from the list.
 */
public class DeleteCommand extends Command {

    private final String fullCommand;

    /**
     * Creates a delete command from the full command text.
     *
     * @param fullCommand the full command text as typed by the user, e.g. "delete 2".
     */
    public DeleteCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Removes the task named in the command from the task list, persists the
     * updated list to disk, and shows a confirmation to the user.
     *
     * @param taskList the task list to remove from.
     * @param ui the Ui to show the confirmation on.
     * @throws YapBotException if no valid task number was given.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        int taskIndex = taskList.validateIndex(fullCommand, "delete");
        Task removedTask = taskList.delete(taskIndex);
        Storage.save(taskList.toArray(), taskList.size());
        ui.showTaskDeleted(removedTask, taskList.size());
    }
}