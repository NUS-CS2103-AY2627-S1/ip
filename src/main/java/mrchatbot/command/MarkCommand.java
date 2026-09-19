package mrchatbot.command;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.storage.Storage;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the given one-based task number.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task, saves the updated task list, and shows the marked-task message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException {
        Task task = tasks.mark(taskNumber);
        storage.saveTasks(tasks);
        ui.showTaskMarked(task);
    }
}
