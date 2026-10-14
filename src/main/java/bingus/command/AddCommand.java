package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Adds one task to the task list and persists the updated list.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADD;
    }

    /**
     * Adds the task, saves the list, and returns the result message.
     *
     * @param tasks task list to update.
     * @param ui user interface used to format the result.
     * @param storage persistent task storage.
     * @return task-added message.
     * @throws BingusException if the updated list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.getTasksSnapshot());
        } catch (BingusException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        return ui.getTaskAddedMessage(task, tasks.size());
    }
}
