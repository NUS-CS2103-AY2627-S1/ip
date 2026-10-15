package command;

import exception.CbtException;
import task.TaskList;

/** Represents one action the user can perform on a task list. */
public interface Command {
    /**
     * Executes this action against the supplied task list.
     *
     * @param taskList task list on which the command operates.
     * @return result containing the message and application state changes.
     * @throws CbtException if the command cannot be completed.
     */
    CommandResult execute(TaskList taskList) throws CbtException;
}
