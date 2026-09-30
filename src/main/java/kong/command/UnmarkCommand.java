package kong.command;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Task;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a command that marks the specified task as not done.
     *
     * @param taskNumber one-based task number entered by the user
     */
    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        try {
            Task task = tasks.get(parseTaskIndex(taskNumber, "unmark"));
            task.unmark();
            ui.showTaskUnmarked(task);
            saveTasks(tasks, storage);
        } catch (IndexOutOfBoundsException e) {
            throw invalidTaskNumberException(tasks);
        }
    }
}
