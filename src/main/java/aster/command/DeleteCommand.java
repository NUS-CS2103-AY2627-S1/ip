package aster.command;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Task;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends IndexedCommand {
    /**
     * Creates a command that removes the task the given text names.
     *
     * @param arguments everything the user typed after the keyword.
     */
    public DeleteCommand(String arguments) {
        super(CommandType.DELETE, arguments);
    }

    /**
     * Removes the named task, reports it with the number of tasks left, and saves the
     * list.
     *
     * <p>The number is checked before anything is removed, so a refused delete leaves
     * the list unchanged.
     *
     * @param tasks the task list to remove from.
     * @param ui the user interface to report through.
     * @param storage the store to save the changed list to.
     * @throws AsterException if the number does not name a task, or if the changed list
     *     cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AsterException {
        Task task = tasks.remove(resolveIndex(tasks));
        ui.showRemoved(task, tasks.size());
        storage.save(tasks.asList());
    }
}
