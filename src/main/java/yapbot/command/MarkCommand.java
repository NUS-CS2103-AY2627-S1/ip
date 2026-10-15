package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "mark" command, which marks a task as done.
 */
public class MarkCommand extends Command {

    private final String fullCommand;

    /**
     * Creates a mark command from the full command text.
     *
     * @param fullCommand the full command text as typed by the user, e.g. "mark 2".
     */
    public MarkCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Marks the task named in the command as done, persists the updated list
     * to disk, and shows a confirmation to the user.
     *
     * @param taskList the task list containing the task to mark.
     * @param ui the Ui to show the confirmation on.
     * @throws YapBotException if no valid task number was given.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        int taskIndex = taskList.validateIndex(fullCommand, "mark");
        taskList.markAsDone(taskIndex);
        Storage.save(taskList.toArray(), taskList.size());
        ui.showTaskMarked(taskList.get(taskIndex));
    }
}