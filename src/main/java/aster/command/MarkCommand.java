package aster.command;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Task;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Marks one task as done.
 */
public class MarkCommand extends IndexedCommand {
    /**
     * Creates a command that marks the task the given text names.
     *
     * @param arguments everything the user typed after the keyword.
     */
    public MarkCommand(String arguments) {
        super(CommandType.MARK, arguments);
    }

    /**
     * Marks the named task as done, reports it, and saves the list.
     *
     * <p>A task that is already done is reported as it stands and nothing is written,
     * since saving a list that has not changed could only rewrite the file with what it
     * already holds.
     *
     * @param tasks the task list holding the task.
     * @param ui the user interface to report through.
     * @param storage the store to save the changed list to.
     * @throws AsterException if the number does not name a task, or if the changed list
     *     cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AsterException {
        Task task = tasks.get(resolveIndex(tasks));
        if (task.isDone()) {
            ui.showAlreadyMarked(task);
            return;
        }
        task.markAsDone();
        ui.showMarked(task);
        storage.save(tasks.asList());
    }
}
