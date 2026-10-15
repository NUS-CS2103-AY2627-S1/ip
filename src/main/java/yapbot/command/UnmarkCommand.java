package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "unmark" command, which marks a task as not done.
 */
public class UnmarkCommand extends Command {

    private final String fullCommand;

    /**
     * Creates an unmark command from the full command text.
     *
     * @param fullCommand the full command text as typed by the user, e.g. "unmark 2".
     */
    public UnmarkCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Marks the task named in the command as not done, persists the updated
     * list to disk, and shows a confirmation to the user.
     *
     * @param taskList the task list containing the task to unmark.
     * @param ui the Ui to show the confirmation on.
     * @throws YapBotException if no valid task number was given.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        int taskIndex = taskList.validateIndex(fullCommand, "unmark");
        taskList.markAsNotDone(taskIndex);
        Storage.save(taskList.toArray(), taskList.size());
        ui.showTaskUnmarked(taskList.get(taskIndex));
    }
}