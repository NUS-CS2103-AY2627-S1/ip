package grug.command;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Represents no command given.
 */
public record EmptyCommand() implements GrugCommand {
    /**
     * Does nothing.
     *
     * @param tasks   The task list that is never used. Can be {@code null}.
     * @param storage The task storage that is never used. Can be {@code null}.
     * @return A {@link CommandResult.Ok} result containing an empty {@code message}
     *         and {@code shouldExit: false}.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        return new CommandResult.Ok("");
    }
}
