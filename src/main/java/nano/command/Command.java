package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.TaskList;

/**
 * Represents a command that can be executed by Nano.
 */
public interface Command {

    /**
     * Executes this command.
     *
     * @param tasks the current task list
     * @param storage the storage used to save tasks
     * @return the response to display to the user
     * @throws NanoException if the command cannot be executed
     */
    String execute(TaskList tasks, Storage storage) throws NanoException;
}
