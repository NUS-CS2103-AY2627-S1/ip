package plana.command;

import plana.client.ClientList;
import plana.exception.PlanaException;
import plana.storage.ClientStorage;
import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Represents one executable command entered by the user.
 *
 * <p>Concrete command classes will gradually take over the command cases that
 * are currently handled directly by Plana.</p>
 */
public abstract class Command {
    /**
     * Executes this command using the application's collaborators.
     *
     * @param tasks the current task list.
     * @param ui the user interface used for responses.
     * @param storage the task storage used for persistence.
     * @throws PlanaException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException;

    /**
     * Executes this command with both task and client collaborators.
     * Existing task commands delegate to their original execution method so
     * their behavior remains unchanged.
     *
     * @param tasks the current task list.
     * @param clients the current client list.
     * @param ui the user interface used for responses.
     * @param storage the task storage used for persistence.
     * @param clientStorage the client storage used for persistence.
     * @throws PlanaException if the command cannot be completed.
     */
    public void execute(TaskList tasks, ClientList clients, Ui ui, Storage storage,
                        ClientStorage clientStorage) throws PlanaException {
        execute(tasks, ui, storage);
    }

    /**
     * Indicates whether executing this command should end the application.
     *
     * @return true when the command exits Plana
     */
    public boolean isExit() {
        return false;
    }
}
