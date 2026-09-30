package kong.command;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Task;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a command that marks the specified task as done.
     *
     * @param taskNumber one-based task number entered by the user
     */
    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        try {
            Task task = tasks.get(parseTaskIndex(taskNumber, "mark"));
            task.mark();
            ui.showTaskMarked(task);
            saveTasks(tasks, storage);
        } catch (IndexOutOfBoundsException e) {
            throw invalidTaskNumberException(tasks);
        }
    }
}
