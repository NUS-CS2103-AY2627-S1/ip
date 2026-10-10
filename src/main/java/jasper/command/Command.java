package jasper.command;

import jasper.JasperException;
import jasper.task.TaskList;

/**
 * Represents an executable command in the application.
 */
public interface Command {
    /**
     * Executes the command using the provided task list.
     *
     * @param tasks List of tasks to operate on.
     * @return CommandResult object generated after execution.
     * @throws JasperException If an error occurs during execution.
     */
    public CommandResult execute(TaskList tasks) throws JasperException;
}
