package dingleberry.command;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Deletes the task at a one-based index parsed from user input. */
public class DeleteCommand extends Command {
    /** Stores the one-based task index to delete. */
    private final int taskNumber;

    /**
     * Creates a command that deletes the given one-based task number.
     *
     * @param taskIndex the one-based index of the task to delete.
     */
    public DeleteCommand(final int taskIndex) {
        this.taskNumber = taskIndex;
    }

    @Override
    public final void execute(final TaskList tasks, final Ui ui,
            final Storage storage)
            throws DingleberryException {
        final int taskIndex = requireTaskIndex(taskNumber, tasks);
        final Task deletedTask = tasks.remove(taskIndex);
        assert deletedTask != null : "A task list must not contain null tasks.";
        ui.showTaskDeleted(deletedTask, tasks.size());
        saveTasks(tasks, storage, ui);
    }
}
