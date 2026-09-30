package kong.command;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Task;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a command that removes the specified task.
     *
     * @param taskNumber one-based task number entered by the user
     */
    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        try {
            int previousTaskCount = tasks.size();
            Task task = tasks.remove(parseTaskIndex(taskNumber, "delete"));
            assert tasks.size() == previousTaskCount - 1 : "Deleting a task must reduce the task count by one";
            ui.showTaskDeleted(task);
            saveTasks(tasks, storage);
        } catch (IndexOutOfBoundsException e) {
            throw invalidTaskNumberException(tasks);
        }
    }
}
