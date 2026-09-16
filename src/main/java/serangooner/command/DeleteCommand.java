package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.Task;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Deletes a task from the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Constructs a command that deletes the task at the given position.
     *
     * @param taskNumber Position of the task in the list, counting from one.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        Task deletedTask = tasks.delete(taskNumber);
        storage.save(tasks.getTasks());
        return ui.formatTaskDeleted(deletedTask);
    }
}
