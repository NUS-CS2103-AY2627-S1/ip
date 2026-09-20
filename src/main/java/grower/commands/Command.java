package grower.commands;

import grower.exceptions.GrowerException;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Executes this command using the application's task data and user interface.
     *
     * @param tasks Task list to read or modify.
     * @param ui User interface used to display the command result.
     * @return Whether the application should continue accepting commands.
     * @throws GrowerException If the command cannot be completed.
     */
    public abstract boolean execute(TaskList tasks, Ui ui) throws GrowerException;
}
