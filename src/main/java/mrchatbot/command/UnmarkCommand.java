package mrchatbot.command;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.storage.Storage;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the given one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks the task, saves the updated task list, and shows the unmarked-task message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException {
        Task task = tasks.unmark(taskNumber);
        storage.saveTasks(tasks);
        ui.showTaskUnmarked(task);
    }
}
