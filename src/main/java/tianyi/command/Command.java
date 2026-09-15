package tianyi.command;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.TaskList;

/**
 * Represents an executable command issued by the user.
 */
public abstract class Command {
    /**
     * Creates a command for execution by the application.
     */
    protected Command() {
    }

    /**
     * Applies this command to the task list and returns its result.
     *
     * @param tasks Task list on which the command operates.
     * @param storage Storage used to persist task changes.
     * @return Response describing the command result.
     * @throws TianyiException If the command cannot be completed.
     */
    public abstract Response execute(TaskList tasks, Storage storage)
            throws TianyiException;
}
