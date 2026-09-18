package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents an executable command entered by the user.
 */
public abstract class Command {

    /**
     * Executes this command using the application's shared collaborators.
     *
     * @param tasks the task list to read or update.
     * @param ui the output interface used to display responses.
     * @param storage the storage used to persist changes.
     * @throws HertaException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, UiOutput ui, Storage storage)
            throws HertaException;

    /**
     * Executes this command using the application's active and archived state.
     * Commands that only operate on active tasks retain their existing execution path.
     *
     * @param repository the repository containing Herta's task collections.
     * @param ui the output interface used to display responses.
     * @throws HertaException if the command cannot be completed.
     */
    public void execute(TaskRepository repository, UiOutput ui) throws HertaException {
        execute(repository.getActiveTasks(), ui, repository.getActiveStorage());
    }
}
