package mrchatbot.command;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.storage.Storage;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private static final int MAX_TASKS = 100;
    private static final String TASK_LIST_FULL_MESSAGE =
            "Sorry, Mr User, your task list is full. No more tasks can be added.";

    private final Task task;

    /**
     * Creates a command that adds the given task.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated task list, and shows the added-task message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException {
        if (tasks.isFull(MAX_TASKS)) {
            throw new MrChatbotException(TASK_LIST_FULL_MESSAGE);
        }
        tasks.add(task);
        storage.saveTasks(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
