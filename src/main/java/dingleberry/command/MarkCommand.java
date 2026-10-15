package dingleberry.command;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Marks the task at a one-based index parsed from user input as done. */
public class MarkCommand extends Command {
    /** Stores the one-based task index to mark. */
    private final int taskNumber;

    /**
     * Creates a command that marks the given one-based task number as done.
     *
     * @param taskIndex the one-based index of the task to mark.
     */
    public MarkCommand(final int taskIndex) {
        this.taskNumber = taskIndex;
    }

    @Override
    public final void execute(final TaskList tasks, final Ui ui,
            final Storage storage)
            throws DingleberryException {
        final int taskIndex = requireTaskIndex(taskNumber, tasks);
        final Task taskToMark = tasks.get(taskIndex);
        assert taskToMark != null : "A task list must not contain null tasks.";
        taskToMark.markAsDone();
        ui.showTaskMarked(taskToMark);
        saveTasks(tasks, storage, ui);
    }
}
