package daddy.command;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Marks one task in the list as done.
 */
public class MarkCommand extends TaskIndexCommand {
    /**
     * Creates a command for the supplied zero-based task index.
     *
     * @param taskIndex the task index to mark
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /** Marks, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DaddyException {
        ensureTaskExists(tasks);
        boolean wasDone = tasks.getTaskAt(getTaskIndex()).isDone();
        Task task = tasks.markTaskAsDone(getTaskIndex());
        try {
            storage.save(tasks);
        } catch (DaddyException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw exception;
        }
        ui.showTaskMarked(task);
    }
}
