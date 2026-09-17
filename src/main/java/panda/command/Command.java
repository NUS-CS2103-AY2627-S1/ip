package panda.command;

import panda.exception.ApplicationException;
import panda.task.TaskList;

/**
 * Represents an action Panda can perform for a trainee's command.
 */
public interface Command {

    /**
     * Performs the command.
     *
     * @param taskList Active task list.
     * @throws ApplicationException If an application error occurs during execution.
     */
    void execute(TaskList taskList) throws ApplicationException;
}
