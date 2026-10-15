package dingleberry.command;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Unmarks the task at a one-based index parsed from user input. */
public class UnmarkCommand extends Command {
    /** Stores the one-based task index to unmark. */
    private final int taskNumber;

    /**
     * Creates a command that unmarks the given one-based task number.
     *
     * @param taskIndex the one-based index of the task to unmark.
     */
    public UnmarkCommand(final int taskIndex) {
        this.taskNumber = taskIndex;
    }

    @Override
    public final void execute(final TaskList tasks, final Ui ui,
            final Storage storage)
            throws DingleberryException {
        final int taskIndex = requireTaskIndex(taskNumber, tasks);
        final Task taskToUnmark = tasks.get(taskIndex);
        assert taskToUnmark != null
                : "A task list must not contain null tasks.";
        taskToUnmark.unmarkDone();
        ui.showTaskUnmarked(taskToUnmark);
        saveTasks(tasks, storage, ui);
    }
}
